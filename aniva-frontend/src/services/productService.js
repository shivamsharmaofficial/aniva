import { apiGet, apiPost, apiPut, apiDelete } from "@/lib/apiClient";

/* ================================
GET PRODUCTS (WITH FILTERS)
================================ */
export const fetchProducts = async (params = {}) => {
const {
page = 0,
size = 10,
sort = "createdAt",
direction = "desc",
...filters
} = params;

const queryParams = new URLSearchParams();

const addParam = (key, value) => {
if (
value !== undefined &&
value !== null &&
(typeof value !== "string" || value.trim() !== "")
) {
if (Array.isArray(value)) {
value.forEach((v) => queryParams.append(key, v));
} else {
queryParams.append(key, value);
}
}
};

// Add filters
Object.entries(filters).forEach(([key, value]) => {
addParam(key, value);
});

// Add defaults
addParam("page", page);
addParam("size", size);
addParam("sort", sort);
addParam("direction", direction);

const query = queryParams.toString();
const url = query ? `/products?${query}` : `/products`;

return await apiGet(url);
};

/* ================================
GET PRODUCT BY SLUG
================================ */
export const fetchProductBySlug = async (slug) => {
  return await apiGet(`/products/${slug}`);
};

/* ================================
GET PRODUCT BY ID
================================ */
export const fetchProductById = async (id) => {
  return await apiGet(`/admin/products/${id}`);
};

/* ================================
ADD REVIEW
================================ */
export const addReview = async (productId, payload) => {
  return await apiPost(`/products/${productId}/reviews`, payload);
};

/* ================================
ADMIN CREATE PRODUCT
================================ */
export const createProduct = async (payload) => {
return await apiPost("/admin/products", payload);
};

/* ================================
ADMIN UPDATE PRODUCT
================================ */
export const updateProduct = async (id, payload) => {
return await apiPut(`/admin/products/${id}`, payload);
};

/* ================================
DELETE PRODUCT
================================ */
export const deleteProduct = async (id) => {
return await apiDelete(`/products/${id}`);
};

/* ================================
RESTORE PRODUCT
================================ */
export const restoreProduct = async (id) => {
return await apiPut(`/products/${id}/restore`);
};

/* ================================
TOGGLE ACTIVE
================================ */
export const toggleProductActive = async (id) => {
return await apiPut(`/products/${id}/toggle-active`);
};
