export interface User {
  id: string;
  email: string;
  firstName: string;
  lastName: string;
  createdAt: string;
}

export interface OrderItem {
  productId: string;
  quantity: number;
}

export interface Order {
  id: string;
  userId: string;
  items: OrderItem[];
  totalAmount: number;
  status: string;
  createdAt?: string;
  updatedAt?: string;
}

export interface Payment {
  id: string;
  orderId: string;
  amount: number;
  status: string;
  transactionReference: string;
  createdAt: string;
}

export interface InventoryItem {
  productId: string;
  availableQuantity: number;
  reservedQuantity: number;
}

export interface Notification {
  id: string;
  orderId: string;
  eventType: string;
  message: string;
  createdAt: string;
}

export interface FraudCheckRequest {
  orderId?: string;
  userId?: string;
  amount?: number;
  currency?: string;
  paymentMethod?: string;
  userCountry?: string;
  merchantCountry?: string;
  userOrderCount?: number;
}

export interface FraudCheckResponse {
  riskLevel: string;
  riskScore: number;
  explanation: string;
  recommendedBlock: boolean;
}

export interface SummarizeRequest {
  orderId?: string;
  orderStatus?: string;
  paymentStatus?: string;
  inventoryStatus?: string;
}

export interface SummarizeResponse {
  summary: string;
  status: string;
  provider: string;
}

export interface ChatRequest {
  question: string;
  context?: string;
}

export interface ChatResponse {
  answer: string;
  provider: string;
}

export interface IngestRequest {
  documentId: string;
  content: string;
  metadata?: string;
}

export interface ServiceConfig {
  host: string;
  port: number;
  useProxy?: boolean;
}

export interface ApiResponse<T = unknown> {
  ok: boolean;
  status: number;
  data?: T;
  error?: string;
}
