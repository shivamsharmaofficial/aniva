import axiosInstance from "@/api/axiosInstance";

/* =========================================================
   UTILITY → CLEAN PARAMS
   Removes null / undefined / empty string
========================================================= */
const cleanParams = (params) =>
  Object.fromEntries(
    Object.entries(params).filter(
      ([, value]) =>
        value !== undefined &&
        value !== null &&
        value !== ""
    )
  );

/* =========================================================
   GET PRODUCTS (ADMIN)
   Used for ManageProducts page
========================================================= */
export const getProducts = async (params = {}) => {
  const {
    category,
    minPrice,
    maxPrice,
    search,
    status,
    includeDeleted,
    sort,
    direction,
    page = 0,
    size = 10,
  } = params;

  const queryParams = cleanParams({
    category:
      Array.isArray(category) && category.length
        ? category
        : undefined,

    minPrice,
    maxPrice,

    search: search?.trim(),

    // Only send status if not ALL
    status:
      status && status !== "ALL"
        ? status
        : undefined,

    // Only send true (never send false)
    includeDeleted:
      includeDeleted === true
        ? true
        : undefined,

    sort: sort || "createdAt",
    direction: direction || "desc",

    page,
    size,
  });

  const response = await axiosInstance.get(
    "/products",
    { params: queryParams }
  );

  return (
    response?.data?.data ?? {
      content: [],
      totalPages: 0,
      totalElements: 0,
    }
  );
};

/* =========================================================
   GET PRODUCT BY SLUG (PUBLIC)
========================================================= */
export const getProductBySlug = async (slug) => {
  const response = await axiosInstance.get(
    `/products/${slug}`
  );

  return response?.data?.data ?? null;
};

/* =========================================================
   DELETE PRODUCT (SOFT DELETE - ADMIN)
========================================================= */
export const deleteProduct = async (id) => {
  const response = await axiosInstance.delete(
    `/products/${id}`
  );

  return response?.data?.data ?? null;
};

/* =========================================================
   RESTORE PRODUCT (ADMIN)
========================================================= */
export const restoreProduct = async (id) => {
  const response = await axiosInstance.patch(
    `/products/${id}/restore`
  );

  return response?.data?.data ?? null;
};

/* =========================================================
   TOGGLE ACTIVE STATUS (ADMIN)
========================================================= */
export const toggleProductActive = async (id) => {
  const response = await axiosInstance.patch(
    `/products/${id}/toggle-active`
  );

  return response?.data?.data ?? null;
};