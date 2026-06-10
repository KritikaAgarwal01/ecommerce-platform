export interface OrderSummary {
  totalOrders: number;
  totalRevenue: number;
  averageOrderValue: number;
  completedOrders: number;
  pendingOrders: number;
}

export interface DailyOrderReport {
  orderDate: string;
  orderCount: number;
  dailyRevenue: number;
}

export interface PaymentSummary {
  totalPayments: number;
  totalProcessed: number;
  successfulPayments: number;
  failedPayments: number;
}

export interface ActiveCartsReport {
  activeCartCount: number;
  reportGeneratedAt: string;
}
