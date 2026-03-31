package com.aniva.modules.cart.service;

import com.aniva.modules.cart.dto.AddToCartRequest;
import com.aniva.modules.cart.dto.CartItemResponse;
import com.aniva.modules.cart.dto.RedisCartItem;
import com.aniva.modules.product.entity.Product;
import com.aniva.modules.product.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class RedisCartService {

    private static final String CART_KEY_PREFIX = "cart:";
    private static final Duration CART_TTL = Duration.ofHours(2);

    private final RedisTemplate<String, Object> redisTemplate;
    private final ProductRepository productRepository;
    private final CartService cartService;
    private final boolean redisEnabled;

    public RedisCartService(
            RedisTemplate<String, Object> redisTemplate,
            ProductRepository productRepository,
            CartService cartService,
            @Value("${app.redis.enabled:false}") boolean redisEnabled) {
        this.redisTemplate = redisTemplate;
        this.productRepository = productRepository;
        this.cartService = cartService;
        this.redisEnabled = redisEnabled;
    }

    public void addToCart(Long userId, AddToCartRequest request) {
        if (!redisEnabled) {
            cartService.addToCart(userId, request);
            return;
        }

        try {
            validateRedisRequest(request.getProductId(), request.getQuantity());
            Product product = productRepository.findById(request.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found"));
            validateAvailableStock(product, request.getQuantity());

            String key = buildCartKey(userId);
            redisTemplate.opsForHash().increment(
                    key,
                    request.getProductId().toString(),
                    request.getQuantity().longValue()
            );
            redisTemplate.expire(key, CART_TTL);
        } catch (Exception ex) {
            ex.printStackTrace();
            cartService.addToCart(userId, request);
        }
    }

    public List<CartItemResponse> getCart(Long userId) {
        if (!redisEnabled) {
            return safeCartFallback(userId);
        }

        try {
            return getRedisCart(userId);
        } catch (Exception ex) {
            ex.printStackTrace();
            return safeCartFallback(userId);
        }
    }

    public void removeItem(Long userId, Long productId) {
        if (!redisEnabled) {
            cartService.removeProduct(userId, productId);
            return;
        }

        try {
            redisTemplate.opsForHash().delete(buildCartKey(userId), productId.toString());
            redisTemplate.expire(buildCartKey(userId), CART_TTL);
        } catch (Exception ex) {
            ex.printStackTrace();
            cartService.removeProduct(userId, productId);
        }
    }

    public void clearCart(Long userId) {
        if (!redisEnabled) {
            cartService.clearCart(userId);
            return;
        }

        try {
            redisTemplate.delete(buildCartKey(userId));
        } catch (Exception ex) {
            ex.printStackTrace();
            cartService.clearCart(userId);
        }
    }

    private List<CartItemResponse> getRedisCart(Long userId) {
        String key = buildCartKey(userId);
        HashOperations<String, Object, Object> hashOperations = redisTemplate.opsForHash();
        Map<Object, Object> entries = hashOperations.entries(key);

        if (entries == null || entries.isEmpty()) {
            List<CartItemResponse> fallbackCart = safeCartFallback(userId);

            if (fallbackCart.isEmpty()) {
                return List.of();
            }

            for (CartItemResponse item : fallbackCart) {
                hashOperations.put(key, item.getProductId().toString(), item.getQuantity());
            }
            redisTemplate.expire(key, CART_TTL);
            return fallbackCart;
        }

        redisTemplate.expire(key, CART_TTL);

        List<RedisCartItem> redisItems = entries.entrySet().stream()
                .map(entry -> new RedisCartItem(
                        Long.valueOf(entry.getKey().toString()),
                        toInteger(entry.getValue())
                ))
                .filter(item -> item.getQuantity() != null && item.getQuantity() > 0)
                .toList();

        if (redisItems.isEmpty()) {
            return List.of();
        }

        Map<Long, Product> productsById = productRepository.findAllById(
                        redisItems.stream()
                                .map(RedisCartItem::getProductId)
                                .toList()
                ).stream()
                .collect(Collectors.toMap(Product::getId, product -> product));

        return redisItems.stream()
                .map(item -> toCartItemResponse(key, hashOperations, productsById.get(item.getProductId()), item))
                .filter(Objects::nonNull)
                .toList();
    }

    private String buildCartKey(Long userId) {
        return CART_KEY_PREFIX + userId;
    }

    private CartItemResponse toCartItemResponse(
            String key,
            HashOperations<String, Object, Object> hashOperations,
            Product product,
            RedisCartItem item) {
        if (product == null) {
            hashOperations.delete(key, item.getProductId().toString());
            return null;
        }

        return new CartItemResponse(
                null,
                product.getId(),
                product.getName(),
                resolveImageUrl(product),
                resolvePrice(product).doubleValue(),
                item.getQuantity()
        );
    }

    private String resolveImageUrl(Product product) {
        if (product.getImages() == null || product.getImages().isEmpty()) {
            return null;
        }
        return product.getImages().get(0).getImageUrl();
    }

    private BigDecimal resolvePrice(Product product) {
        return product.getDiscountPrice() != null
                ? product.getDiscountPrice()
                : product.getPrice();
    }

    private void validateRedisRequest(Long productId, Integer quantity) {
        if (productId == null) {
            throw new RuntimeException("Product ID is required");
        }
        if (quantity == null || quantity <= 0) {
            throw new RuntimeException("Quantity must be greater than zero");
        }
    }

    private void validateAvailableStock(Product product, Integer quantity) {
        int totalStock = product.getTotalStock() == null ? 0 : product.getTotalStock();
        int reservedStock = product.getReservedStock() == null ? 0 : product.getReservedStock();
        int availableStock = totalStock - reservedStock;

        if (quantity > availableStock) {
            throw new RuntimeException("Not enough stock available");
        }
    }

    private Integer toInteger(Object value) {
        if (value == null) {
            return 0;
        }
        if (value instanceof Number number) {
            return number.intValue();
        }
        return Integer.parseInt(value.toString());
    }

    private List<CartItemResponse> safeCartFallback(Long userId) {
        try {
            List<CartItemResponse> cart = cartService.getCart(userId);
            return cart == null ? List.of() : cart;
        } catch (Exception ex) {
            ex.printStackTrace();
            return List.of();
        }
    }
}
