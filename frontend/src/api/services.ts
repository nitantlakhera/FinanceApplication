import { apiCall, buildUrl } from "./client";
import {
  User,
  Order,
  Payment,
  InventoryItem,
  Notification,
  FraudCheckRequest,
  FraudCheckResponse,
  SummarizeRequest,
  SummarizeResponse,
  ChatRequest,
  ChatResponse,
  IngestRequest,
  ServiceConfig,
} from "../types";

export const userService = (config: ServiceConfig) => ({
  create: (email: string, firstName: string, lastName: string) =>
    apiCall<User>("post", buildUrl("user", config, "/users"), {
      email,
      firstName,
      lastName,
    }),
  getById: (id: string) =>
    apiCall<User>("get", buildUrl("user", config, `/users/${id}`)),
});

export const inventoryService = (config: ServiceConfig) => ({
  addStock: (productId: string, quantity: number) =>
    apiCall<InventoryItem>("post", buildUrl("inventory", config, "/inventory"), {
      productId,
      quantity,
    }),
  list: () => apiCall<InventoryItem[]>("get", buildUrl("inventory", config, "/inventory")),
  getById: (productId: string) =>
    apiCall<InventoryItem>("get", buildUrl("inventory", config, `/inventory/${productId}`)),
});

export const orderService = (config: ServiceConfig) => ({
  create: (userId: string, items: { productId: string; quantity: number }[], totalAmount: number) =>
    apiCall<Order>("post", buildUrl("order", config, "/orders"), {
      userId,
      items,
      totalAmount,
    }),
  getById: (id: string) =>
    apiCall<Order>("get", buildUrl("order", config, `/orders/${id}`)),
  getByUser: (userId: string) =>
    apiCall<Order[]>("get", buildUrl("order", config, `/orders/user/${userId}`)),
});

export const paymentService = (config: ServiceConfig) => ({
  list: () => apiCall<Payment[]>("get", buildUrl("payment", config, "/payments")),
  getByOrderId: (orderId: string) =>
    apiCall<Payment>("get", buildUrl("payment", config, `/payments/order/${orderId}`)),
});

export const notificationService = (config: ServiceConfig) => ({
  list: () =>
    apiCall<Notification[]>("get", buildUrl("notification", config, "/notifications")),
  getByOrderId: (orderId: string) =>
    apiCall<Notification[]>(
      "get",
      buildUrl("notification", config, `/notifications/order/${orderId}`)
    ),
});

export const aiService = (config: ServiceConfig) => ({
  fraudCheck: (request: FraudCheckRequest) =>
    apiCall<FraudCheckResponse>(
      "post",
      buildUrl("ai", config, "/ai/fraud-check"),
      request
    ),
  summarize: (request: SummarizeRequest) =>
    apiCall<SummarizeResponse>(
      "post",
      buildUrl("ai", config, "/ai/summarize-order"),
      request
    ),
  ask: (request: ChatRequest) =>
    apiCall<ChatResponse>("post", buildUrl("ai", config, "/ai/ask"), request),
  ingest: (request: IngestRequest) =>
    apiCall<string>("post", buildUrl("ai", config, "/ai/ingest"), request),
  provider: () => apiCall<string>("get", buildUrl("ai", config, "/ai/provider")),
});
