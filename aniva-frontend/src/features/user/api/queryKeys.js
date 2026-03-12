export const userKeys = {
  all: ["user"],
  profile: () => [...userKeys.all, "profile"],
  addresses: () => [...userKeys.all, "addresses"],
  orders: () => [...userKeys.all, "orders"],
  wishlist: () => [...userKeys.all, "wishlist"],
};