package com.aniva.modules.payment.service;

import com.aniva.modules.order.entity.OrderItem;
import com.aniva.modules.order.entity.UserOrder;
import com.aniva.modules.order.enums.OrderStatus;
import com.aniva.modules.order.repository.OrderItemRepository;
import com.aniva.modules.order.repository.UserOrderRepository;
import com.aniva.modules.payment.enums.PaymentStatus;
import com.aniva.modules.inventory.service.InventoryService;
import com.aniva.modules.system.entity.SystemSetting;
import com.aniva.modules.system.repository.SystemSettingRepository;
import com.aniva.modules.system.service.EmailService;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import jakarta.transaction.Transactional;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.StringUtils;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;

@Service
public class PaymentService {

    private static final Logger log = LoggerFactory.getLogger(PaymentService.class);

    private final UserOrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final InventoryService inventoryService;
    private final SystemSettingRepository settingRepository;
    private final EmailService emailService;
    private final EntityManager entityManager;
    private final RazorpayClient razorpayClient;
    private final String razorpaySecret;

    public PaymentService(
            UserOrderRepository orderRepository,
            OrderItemRepository orderItemRepository,
            InventoryService inventoryService,
            SystemSettingRepository settingRepository,
            EmailService emailService,
            EntityManager entityManager,
            RazorpayClient razorpayClient,
            @Value("${razorpay.secret}") String razorpaySecret) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.inventoryService = inventoryService;
        this.settingRepository = settingRepository;
        this.emailService = emailService;
        this.entityManager = entityManager;
        this.razorpayClient = razorpayClient;
        this.razorpaySecret = razorpaySecret;
    }

    public String getPaymentMode() {
        return settingRepository
                .findByKey("PAYMENT_MODE")
                .map(SystemSetting::getValue)
                .orElse("MOCK");
    }

    @Retryable(maxAttempts = 3, backoff = @Backoff(delay = 2000))
    public String createPayment(Long orderId) {

        if (orderId == null) {
            throw new IllegalArgumentException("Order ID is required");
        }

        UserOrder order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalStateException("Order not found"));

        try {
            JSONObject options = new JSONObject();
            options.put("amount", order.getTotalAmount().multiply(BigDecimal.valueOf(100)));
            options.put("currency", "INR");
            options.put("receipt", order.getOrderNumber());
            options.put("notes", new JSONObject().put("internalOrderId", orderId));

            Order razorOrder = razorpayClient.orders.create(options);
            return razorOrder.toString();
        } catch (Exception ex) {
            throw new IllegalStateException("Razorpay order creation failed", ex);
        }
    }

    public void verifyWebhookSignature(String payload, String signature) {

        if (!StringUtils.hasText(payload) || !StringUtils.hasText(signature)) {
            throw new IllegalArgumentException("Invalid webhook payload");
        }

        try {
            String expected = hmacSha256(payload, razorpaySecret);
            if (!expected.equals(signature)) {
                throw new IllegalStateException("Invalid Razorpay webhook signature");
            }
        } catch (IllegalStateException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new IllegalStateException("Webhook signature verification failed", ex);
        }
    }

    public UserOrder handleCapturedPaymentWebhook(String payload, String signature) {

        verifyWebhookSignature(payload, signature);

        JSONObject event = new JSONObject(payload);
        String eventType = event.optString("event");
        if (!"payment.captured".equals(eventType)) {
            return null;
        }

        JSONObject paymentEntity = event.optJSONObject("payload")
                .optJSONObject("payment")
                .optJSONObject("entity");

        if (paymentEntity == null) {
            throw new IllegalArgumentException("Invalid payment.captured payload");
        }

        Long orderId = resolveWebhookOrderId(paymentEntity);
        String paymentId = paymentEntity.optString("id");

        if (orderId == null || !StringUtils.hasText(paymentId)) {
            throw new IllegalStateException("Unable to resolve webhook order");
        }

        UserOrder existing = findOrderByPaymentId(paymentId);
        if (existing != null) {
            return existing;
        }

        return confirmPayment(orderId, paymentId);
    }

    @Recover
    public String recoverCreatePayment(Exception ex, Long orderId) {
        log.error("payment_order_creation_failed orderId={} message={}", orderId, ex.getMessage(), ex);
        throw new IllegalStateException("Razorpay order creation failed after retries", ex);
    }

    public void verifyRazorpaySignature(
            String razorpayOrderId,
            String paymentId,
            String razorpaySignature
    ) {

        if (!StringUtils.hasText(razorpayOrderId)
                || !StringUtils.hasText(paymentId)
                || !StringUtils.hasText(razorpaySignature)) {
            throw new IllegalArgumentException("Invalid Razorpay payload");
        }

        try {
            String payload = razorpayOrderId + "|" + paymentId;
            String expected = hmacSha256(payload, razorpaySecret);

            if (!expected.equals(razorpaySignature)) {
                throw new IllegalStateException("Invalid Razorpay signature");
            }
        } catch (IllegalStateException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new IllegalStateException("Signature verification failed", ex);
        }
    }

    private String hmacSha256(String data, String secret) throws Exception {

        Mac mac = Mac.getInstance("HmacSHA256");
        SecretKeySpec key = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        mac.init(key);

        byte[] hash = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));

        StringBuilder hex = new StringBuilder(hash.length * 2);
        for (byte b : hash) {
            String s = Integer.toHexString(0xff & b);
            if (s.length() == 1) {
                hex.append('0');
            }
            hex.append(s);
        }

        return hex.toString();
    }

    @Transactional
    public UserOrder confirmPayment(Long orderId, String paymentId) {

        validate(orderId, paymentId);

        UserOrder existing = findOrderByPaymentId(paymentId);
        if (existing != null) {
            return handleExisting(existing, orderId, paymentId);
        }

        UserOrder order = entityManager.find(
                UserOrder.class,
                orderId,
                LockModeType.PESSIMISTIC_WRITE
        );

        if (order == null) {
            throw new IllegalStateException("Order not found");
        }

        UserOrder again = findOrderByPaymentId(paymentId);
        if (again != null) {
            return handleExisting(again, orderId, paymentId);
        }

        if (isPaid(order)) {
            throw new IllegalStateException("Order already paid");
        }

        finalizePayment(order, paymentId);
        sendEmailAfterCommit(order);

        return orderRepository.save(order);
    }

    private void finalizePayment(UserOrder order, String paymentId) {

        order.setPaymentId(paymentId);
        order.setPaymentStatus(PaymentStatus.PAID);
        order.setStatus(OrderStatus.CONFIRMED);

        List<OrderItem> items = orderItemRepository.findByOrderId(order.getId());

        for (OrderItem item : items) {
            inventoryService.confirmStock(
                    item.getProductId(),
                    item.getQuantity(),
                    order.getId(),
                    "PAYMENT_SUCCESS"
            );
        }
    }

    @Transactional
    public void handlePaymentFailure(Long orderId) {

        UserOrder order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalStateException("Order not found"));

        List<OrderItem> items = orderItemRepository.findByOrderId(order.getId());

        for (OrderItem item : items) {
            inventoryService.releaseStock(
                    item.getProductId(),
                    item.getQuantity(),
                    order.getId(),
                    "PAYMENT_FAILURE"
            );
        }
    }

    private void sendEmailAfterCommit(UserOrder order) {

        TransactionSynchronizationManager.registerSynchronization(
                new TransactionSynchronization() {
                    @Override
                    public void afterCommit() {
                        emailService.sendOrderConfirmation(
                                order.getUser().getEmail(),
                                order.getOrderNumber()
                        );
                    }
                }
        );
    }

    private void validate(Long orderId, String paymentId) {
        if (orderId == null) {
            throw new IllegalArgumentException("Order ID required");
        }
        if (!StringUtils.hasText(paymentId)) {
            throw new IllegalArgumentException("Payment ID required");
        }
    }

    private UserOrder handleExisting(UserOrder existing, Long orderId, String paymentId) {

        if (!Objects.equals(existing.getId(), orderId)) {
            throw new IllegalStateException("Payment ID used for another order");
        }

        if (paymentId.equals(existing.getPaymentId())
                && existing.getPaymentStatus() == PaymentStatus.PAID) {
            return existing;
        }

        throw new IllegalStateException("Payment already processing");
    }

    private UserOrder findOrderByPaymentId(String paymentId) {

        return entityManager.createQuery(
                        "SELECT o FROM UserOrder o WHERE o.paymentId = :pid",
                        UserOrder.class
                )
                .setParameter("pid", paymentId)
                .getResultStream()
                .findFirst()
                .orElse(null);
    }

    private boolean isPaid(UserOrder order) {
        return order.getPaymentStatus() == PaymentStatus.PAID
                || order.getStatus() == OrderStatus.CONFIRMED;
    }
    private Long resolveWebhookOrderId(JSONObject paymentEntity) {

        JSONObject notes = paymentEntity.optJSONObject("notes");
        if (notes != null) {
            String internalOrderId = notes.optString("internalOrderId");
            if (!internalOrderId.isBlank()) {
                return Long.valueOf(internalOrderId);
            }

            String orderId = notes.optString("orderId");
            if (!orderId.isBlank()) {
                return Long.valueOf(orderId);
            }
        }

        String receipt = paymentEntity.optString("receipt");
        if (StringUtils.hasText(receipt)) {
            return orderRepository.findByOrderNumber(receipt)
                    .map(UserOrder::getId)
                    .orElse(null);
        }

        return null;
    }
}
