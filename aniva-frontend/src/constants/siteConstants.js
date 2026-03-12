export const BRAND_NAME = "ANIVA";

export const API_BASE_URL =
  import.meta.env.VITE_API_URL || "http://localhost:8080";

export const ROUTES = {
  home: "/",
  candles: "/candles",
  bestSellers: "/bestsellers",
  gifts: "/gifts",
  about: "/about",
  wishlist: "/wishlist",
  cart: "/cart",
  login: "/account/login",
  orders: "/orders",
  accountProfile: "/account/profile",
  accountSettings: "/account/settings",
  collectionsCandles: "/collections/candles",
  collectionsUnder500: "/collections/candles-under-500",
  collectionsBestSellers: "/collections/bestsellers",
  collectionsEssentialOils: "/collections/essential-oils",
  collectionsAromaOils: "/collections/aroma-oils",
  collectionsNew: "/collections/new",
};

export const OFFER_MESSAGES = [
  { text: "Shop Candle Collections", link: ROUTES.collectionsCandles },
  { text: "Candles Under INR 500", link: ROUTES.collectionsUnder500 },
  { text: "Best Seller Candles", link: ROUTES.collectionsBestSellers },
  {
    text: "Essential Oils Collection",
    link: ROUTES.collectionsEssentialOils,
  },
  {
    text: "Aroma Oils Collection",
    link: ROUTES.collectionsAromaOils,
  },
  { text: "New Arrivals", link: ROUTES.collectionsNew },
];
