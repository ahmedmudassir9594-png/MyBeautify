import { Router, type IRouter } from "express";
import {
  AddCartItemBody,
  AddCartItemResponse,
  AddWishlistItemBody,
  AddWishlistItemResponse,
  AuthResponse,
  Cart,
  ClearCartResponse,
  CreateOrderBody,
  CreateOrderResponse,
  GetBrandsResponse,
  GetCartResponse,
  GetCategoriesResponse,
  GetOrdersResponse,
  GetProductResponse,
  GetProductReviewsResponse,
  GetProductsQueryParams,
  GetProductsResponse,
  GetWishlistResponse,
  LoginBody,
  LoginResponse,
  RegisterBody,
  RegisterResponse,
  RemoveCartItemParams,
  RemoveCartItemResponse,
  RemoveWishlistItemParams,
  RemoveWishlistItemResponse,
  UpdateCartItemBody,
  UpdateCartItemParams,
  UpdateCartItemResponse,
  ValidateCouponBody,
  ValidateCouponResponse,
} from "@workspace/api-zod";

const router: IRouter = Router();

const products = [
  {
    id: "dew-cream",
    name: "Dew Cloud Barrier Cream",
    brand: "Kindred Skin",
    category: "Skincare",
    description:
      "A soft, cushiony moisturizer that leaves the skin looking rested, plump, and quietly luminous.",
    price: 34,
    compareAtPrice: 42,
    rating: 4.9,
    reviews: 128,
    image:
      "https://images.unsplash.com/photo-1611930022073-b7a4ba5fcccd?auto=format&fit=crop&w=900&q=85",
    badge: "Bestseller",
    stock: 18,
    ingredients: ["Ceramides", "Squalane", "Beta-glucan"],
    benefits: ["Reinforces the skin barrier", "Long-lasting hydration"],
    sizes: ["50 ml"],
  },
  {
    id: "rose-cleanser",
    name: "Rose Milk Cleanser",
    brand: "Morrow",
    category: "Skincare",
    description:
      "A milky, fragrance-light cleanser that lifts the day away without leaving skin tight.",
    price: 28,
    compareAtPrice: null,
    rating: 4.8,
    reviews: 86,
    image:
      "https://images.unsplash.com/photo-1556228720-195a672e8a03?auto=format&fit=crop&w=900&q=85",
    badge: "New",
    stock: 24,
    ingredients: ["Rose water", "Oat lipids", "Glycerin"],
    benefits: ["Calms visible redness", "Preserves natural moisture"],
    sizes: ["150 ml"],
  },
  {
    id: "silk-blush",
    name: "Silk Veil Cream Blush",
    brand: "Maison Miel",
    category: "Makeup",
    description:
      "A fingertip-friendly wash of color that melts into cheeks for a just-back-from-a-walk flush.",
    price: 24,
    compareAtPrice: 29,
    rating: 4.7,
    reviews: 64,
    image:
      "https://images.unsplash.com/photo-1583241800698-e8ab01830a07?auto=format&fit=crop&w=900&q=85",
    badge: "Editor pick",
    stock: 31,
    ingredients: ["Jojoba oil", "Vitamin E", "Mineral pigments"],
    benefits: ["Buildable color", "Soft satin finish"],
    sizes: ["4.5 g"],
  },
  {
    id: "gloss-oil",
    name: "Gloss Ritual Lip Oil",
    brand: "Onda",
    category: "Makeup",
    description:
      "A glassy lip oil with a nourishing slip and the sheerest veil of rosewood tint.",
    price: 19,
    compareAtPrice: null,
    rating: 4.8,
    reviews: 91,
    image:
      "https://images.unsplash.com/photo-1586495777744-4413f21062fa?auto=format&fit=crop&w=900&q=85",
    badge: null,
    stock: 42,
    ingredients: ["Camellia oil", "Marula oil", "Vitamin C"],
    benefits: ["Softens dry lips", "Reflective, non-sticky shine"],
    sizes: ["5 ml"],
  },
  {
    id: "ritual-body-oil",
    name: "Afterlight Body Oil",
    brand: "Field Notes",
    category: "Body",
    description:
      "A dry-touch botanical oil made for the small ritual at the end of a long day.",
    price: 38,
    compareAtPrice: null,
    rating: 4.9,
    reviews: 57,
    image:
      "https://images.unsplash.com/photo-1608248597279-f99d160bfcbc?auto=format&fit=crop&w=900&q=85",
    badge: "Small batch",
    stock: 12,
    ingredients: ["Meadowfoam seed oil", "Calendula", "Neroli"],
    benefits: ["Leaves skin supple", "Fast-absorbing finish"],
    sizes: ["100 ml"],
  },
  {
    id: "scalp-serum",
    name: "Root Reset Scalp Serum",
    brand: "Serein",
    category: "Haircare",
    description:
      "A cooling, weightless serum for a clean-feeling scalp and softer, shinier roots.",
    price: 31,
    compareAtPrice: 36,
    rating: 4.6,
    reviews: 43,
    image:
      "https://images.unsplash.com/photo-1522337360788-8b13dee7a37e?auto=format&fit=crop&w=900&q=85",
    badge: null,
    stock: 20,
    ingredients: ["Niacinamide", "Peppermint", "Peptides"],
    benefits: ["Refreshes the scalp", "Supports healthy-looking hair"],
    sizes: ["60 ml"],
  },
  {
    id: "cloud-mask",
    name: "Sunday Cloud Recovery Mask",
    brand: "Kindred Skin",
    category: "Skincare",
    description:
      "A replenishing overnight mask for the mornings when your skin needs a little more kindness.",
    price: 40,
    compareAtPrice: null,
    rating: 4.9,
    reviews: 74,
    image:
      "https://images.unsplash.com/photo-1598440947619-2c35fc9aa908?auto=format&fit=crop&w=900&q=85",
    badge: "New",
    stock: 16,
    ingredients: ["Peptides", "Hyaluronic acid", "Murumuru butter"],
    benefits: ["Restores bounce", "Comforts dry, tired skin"],
    sizes: ["75 ml"],
  },
  {
    id: "amber-mist",
    name: "Amber Skin Veil",
    brand: "Morrow",
    category: "Body",
    description:
      "A warm, sheer skin mist that leaves a barely-there glow and a soft amber trail.",
    price: 26,
    compareAtPrice: null,
    rating: 4.5,
    reviews: 38,
    image:
      "https://images.unsplash.com/photo-1563170351-be82bc888aa4?auto=format&fit=crop&w=900&q=85",
    badge: null,
    stock: 27,
    ingredients: ["Aloe", "Glycerin", "Amber accord"],
    benefits: ["Freshens skin", "Layers with fragrance"],
    sizes: ["100 ml"],
  },
] as const;

const categories = [
  {
    id: "skincare",
    name: "Skincare",
    description: "Barrier-first essentials for skin that looks well rested.",
    image:
      "https://images.unsplash.com/photo-1571781926291-c477ebfd024b?auto=format&fit=crop&w=1100&q=85",
    productCount: 3,
  },
  {
    id: "makeup",
    name: "Makeup",
    description: "Soft color and easy formulas for your everyday face.",
    image:
      "https://images.unsplash.com/photo-1512496015851-a90fb38ba796?auto=format&fit=crop&w=1100&q=85",
    productCount: 2,
  },
  {
    id: "body",
    name: "Body",
    description: "A little more ritual for the skin below the neck.",
    image:
      "https://images.unsplash.com/photo-1600428877878-1a0c7d0c7d80?auto=format&fit=crop&w=1100&q=85",
    productCount: 2,
  },
  {
    id: "haircare",
    name: "Haircare",
    description: "Quietly effective care for scalp, roots, and lengths.",
    image:
      "https://images.unsplash.com/photo-1522338242992-e1a54906a8da?auto=format&fit=crop&w=1100&q=85",
    productCount: 1,
  },
];

const brands = [
  {
    id: "kindred-skin",
    name: "Kindred Skin",
    description: "Thoughtful barrier care with a softer point of view.",
    logo: "KS",
    productCount: 2,
  },
  {
    id: "morrow",
    name: "Morrow",
    description: "Modern essentials made for slow, steady routines.",
    logo: "MO",
    productCount: 2,
  },
  {
    id: "maison-miel",
    name: "Maison Miel",
    description: "A little color, a lot of ease.",
    logo: "MM",
    productCount: 1,
  },
  {
    id: "field-notes",
    name: "Field Notes",
    description: "Botanical body care for everyday rituals.",
    logo: "FN",
    productCount: 1,
  },
  {
    id: "serein",
    name: "Serein",
    description: "Clean, cooling care for the scalp and hair.",
    logo: "SR",
    productCount: 1,
  },
  {
    id: "onda",
    name: "Onda",
    description: "Sheer, nourishing color with a glossy finish.",
    logo: "ON",
    productCount: 1,
  },
];

const reviews = {
  "dew-cream": [
    {
      id: "review-1",
      author: "Amelia R.",
      rating: 5,
      title: "The one I keep reaching for",
      body: "It feels comforting without being heavy. My skin has looked noticeably calmer since I started using it.",
      date: "2026-08-14",
    },
    {
      id: "review-2",
      author: "Nadia K.",
      rating: 5,
      title: "Soft, not shiny",
      body: "The finish is beautiful under makeup and my dry patches are finally less visible.",
      date: "2026-07-29",
    },
  ],
  "silk-blush": [
    {
      id: "review-3",
      author: "Maya L.",
      rating: 5,
      title: "Very forgiving",
      body: "A tiny dab is enough. It blends before I have time to overthink it.",
      date: "2026-08-02",
    },
  ],
} as const;

type CartItemState = { productId: string; quantity: number };
type UserRole = "USER" | "ADMIN";
type AppUser = {
  id: string;
  name: string;
  email: string;
  password: string;
  role: UserRole;
};

const users: AppUser[] = [
  {
    id: "demo-user",
    name: "Alicia Bloom",
    email: "customer@mybeautify.com",
    password: "password123",
    role: "USER",
  },
  {
    id: "demo-admin",
    name: "Maya Hart",
    email: "admin@mybeautify.com",
    password: "admin123",
    role: "ADMIN",
  },
];

let cartItems: CartItemState[] = [];
let wishlistIds: string[] = ["dew-cream"];
let orders = [
  {
    id: "MB-2048",
    createdAt: "2026-09-12",
    status: "Delivered",
    total: 62,
    itemCount: 2,
  },
];

function findProduct(id: string) {
  return products.find((product) => product.id === id);
}

function getUserByCredentials(emailOrUsername: string, password: string) {
  const cleanedEmail = emailOrUsername.trim().toLowerCase();
  return users.find(
    (user) =>
      (user.email.toLowerCase() === cleanedEmail ||
        user.name.toLowerCase() === cleanedEmail ||
        user.id.toLowerCase() === cleanedEmail) &&
      user.password === password,
  );
}

function getUserFromRequest(req: { headers: Record<string, string | string[] | undefined> }) {
  const header = req.headers.authorization;
  const tokenValue = Array.isArray(header) ? header[0] : header;
  if (!tokenValue || !tokenValue.startsWith("Bearer ")) return null;
  const token = tokenValue.slice("Bearer ".length).trim();
  const user = users.find((candidate) => {
    if (candidate.role === "ADMIN") return token === "demo-admin-token";
    return token === "demo-user-token";
  });
  return user ?? null;
}

function toAdminProductPayload(payload: Record<string, unknown>) {
  const productId = String(payload.id ?? payload.slug ?? `product-${Date.now()}`)
    .toLowerCase()
    .replace(/[^a-z0-9]+/g, "-")
    .replace(/^-+|-+$/g, "") || `product-${Date.now()}`;

  const mapped = {
    id: productId,
    name: String(payload.name ?? "Untitled product"),
    brand: String(payload.brand ?? payload.brandId ?? "MyBeautify"),
    category: String(payload.category ?? payload.categoryId ?? "Skincare"),
    description: String(payload.description ?? "Premium beauty essentials."),
    price: Number(payload.price ?? 0),
    compareAtPrice: payload.compareAtPrice != null ? Number(payload.compareAtPrice) : null,
    rating: Number(payload.rating ?? 4.8),
    reviews: Number(payload.reviews ?? 0),
    image:
      typeof payload.imageUrl === "string" && payload.imageUrl
        ? payload.imageUrl
        : typeof payload.image === "string" && payload.image
          ? payload.image
          : "https://images.unsplash.com/photo-1522335789203-aabd1fc54bc9?auto=format&fit=crop&w=900&q=80",
    badge: payload.badge ? String(payload.badge) : null,
    stock: Number(payload.stockQuantity ?? payload.stock ?? 0),
    ingredients: Array.isArray(payload.ingredients)
      ? payload.ingredients.map((item) => String(item))
      : typeof payload.ingredients === "string"
        ? payload.ingredients.split(",").map((item) => item.trim()).filter(Boolean)
        : [],
    benefits: Array.isArray(payload.benefits)
      ? payload.benefits.map((item) => String(item))
      : typeof payload.benefits === "string"
        ? payload.benefits.split(",").map((item) => item.trim()).filter(Boolean)
        : [],
    sizes: Array.isArray(payload.sizes)
      ? payload.sizes.map((item) => String(item))
      : typeof payload.size === "string" && payload.size
        ? [payload.size]
        : ["Standard"],
  };

  return mapped;
}

function buildCart() {
  const items = cartItems.flatMap((item) => {
    const product = findProduct(item.productId);
    if (!product) return [];
    return [
      {
        product,
        quantity: item.quantity,
        lineTotal: Number((product.price * item.quantity).toFixed(2)),
      },
    ];
  });
  const subtotal = Number(
    items.reduce((sum, item) => sum + item.lineTotal, 0).toFixed(2),
  );
  const shipping = subtotal === 0 || subtotal >= 60 ? 0 : 6;
  const tax = Number((subtotal * 0.08).toFixed(2));
  const cart = {
    items,
    subtotal,
    shipping,
    tax,
    total: Number((subtotal + shipping + tax).toFixed(2)),
    couponDiscount: 0,
  };
  return cart;
}

function wishlistProducts() {
  return products.filter((product) => wishlistIds.includes(product.id));
}

router.get("/products", (req, res) => {
  const query = GetProductsQueryParams.parse(req.query);
  const search = query.search?.toLowerCase();
  let result = products.filter((product) => {
    const matchesSearch =
      !search ||
      [product.name, product.brand, product.category, product.description]
        .join(" ")
        .toLowerCase()
        .includes(search);
    const matchesCategory =
      !query.category ||
      product.category.toLowerCase() === query.category.toLowerCase();
    const matchesBrand =
      !query.brand ||
      product.brand.toLowerCase() === query.brand.toLowerCase();
    return matchesSearch && matchesCategory && matchesBrand;
  });
  if (query.sort === "price-low") result = [...result].sort((a, b) => a.price - b.price);
  if (query.sort === "price-high") result = [...result].sort((a, b) => b.price - a.price);
  if (query.sort === "rating") result = [...result].sort((a, b) => b.rating - a.rating);
  if (query.sort === "newest") result = [...result].reverse();
  res.json(GetProductsResponse.parse(result));
});

router.get("/products/:id", (req, res) => {
  const product = findProduct(req.params.id);
  if (!product) {
    res.status(404).json({ error: "Product not found" });
    return;
  }
  res.json(GetProductResponse.parse(product));
});

router.get("/products/:id/reviews", (req, res) => {
  res.json(GetProductReviewsResponse.parse(reviews[req.params.id as keyof typeof reviews] ?? []));
});

router.get("/categories", (_req, res) => {
  res.json(GetCategoriesResponse.parse(categories));
});

router.get("/brands", (_req, res) => {
  res.json(GetBrandsResponse.parse(brands));
});

router.post("/auth/login", (req, res) => {
  const body = LoginBody.parse(req.body);
  const name = body.email.split("@")[0] || "Beauty lover";
  res.json(
    LoginResponse.parse({
      user: { id: "demo-user", name: name.replace(/[._-]/g, " "), email: body.email },
      token: "demo-token",
    }),
  );
});

router.post("/auth/register", (req, res) => {
  const body = RegisterBody.parse(req.body);
  res.status(201).json(
    RegisterResponse.parse({
      user: { id: "new-user", name: body.name, email: body.email },
      token: "demo-token",
    }),
  );
});

router.get("/cart", (_req, res) => {
  res.json(GetCartResponse.parse(buildCart()));
});

router.delete("/cart", (_req, res) => {
  cartItems = [];
  res.json(ClearCartResponse.parse(buildCart()));
});

router.post("/cart/items", (req, res) => {
  const body = AddCartItemBody.parse(req.body);
  const product = findProduct(body.productId);
  if (!product) {
    res.status(404).json({ error: "Product not found" });
    return;
  }
  const existing = cartItems.find((item) => item.productId === body.productId);
  if (existing) existing.quantity = Math.min(existing.quantity + body.quantity, 10);
  else cartItems.push({ productId: body.productId, quantity: body.quantity });
  res.json(AddCartItemResponse.parse(buildCart()));
});

router.patch("/cart/items/:productId", (req, res) => {
  const params = UpdateCartItemParams.parse(req.params);
  const body = UpdateCartItemBody.parse(req.body);
  const item = cartItems.find((candidate) => candidate.productId === params.productId);
  if (!item) {
    res.status(404).json({ error: "Cart item not found" });
    return;
  }
  item.quantity = body.quantity;
  res.json(UpdateCartItemResponse.parse(buildCart()));
});

router.delete("/cart/items/:productId", (req, res) => {
  const params = RemoveCartItemParams.parse(req.params);
  cartItems = cartItems.filter((item) => item.productId !== params.productId);
  res.json(RemoveCartItemResponse.parse(buildCart()));
});

router.get("/wishlist", (_req, res) => {
  res.json(GetWishlistResponse.parse(wishlistProducts()));
});

router.post("/wishlist", (req, res) => {
  const body = AddWishlistItemBody.parse(req.body);
  if (findProduct(body.productId) && !wishlistIds.includes(body.productId)) wishlistIds.push(body.productId);
  res.json(AddWishlistItemResponse.parse(wishlistProducts()));
});

router.delete("/wishlist/:productId", (req, res) => {
  const params = RemoveWishlistItemParams.parse(req.params);
  wishlistIds = wishlistIds.filter((id) => id !== params.productId);
  res.json(RemoveWishlistItemResponse.parse(wishlistProducts()));
});

router.post("/coupons/validate", (req, res) => {
  const body = ValidateCouponBody.parse(req.body);
  const code = body.code.trim().toUpperCase();
  const valid = code === "GLOW15";
  res.json(
    ValidateCouponResponse.parse({
      valid,
      code,
      discount: valid ? 15 : 0,
      message: valid ? "15% off your ritual" : "That code is not active",
    }),
  );
});

router.get("/orders", (_req, res) => {
  res.json(GetOrdersResponse.parse(orders));
});

router.post("/orders", (req, res) => {
  const body = CreateOrderBody.parse(req.body);
  const total = buildCart().total;
  const order = {
    id: `MB-${Math.floor(1000 + Math.random() * 8999)}`,
    createdAt: new Date().toISOString().slice(0, 10),
    status: "Processing",
    total,
    itemCount: body.items.reduce((sum, item) => sum + item.quantity, 0),
  };
  orders = [order, ...orders];
  cartItems = [];
  res.status(201).json(CreateOrderResponse.parse(order));
});

export default router;