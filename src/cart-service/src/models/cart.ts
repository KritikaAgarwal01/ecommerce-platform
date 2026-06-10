export interface CartItem {
  productId: string;
  productName: string;
  quantity: number;
  unitPrice: number;
  addedAt: string;
}

export interface Cart {
  id: string;
  userId: string;
  items: CartItem[];
  totalPrice: number;
  updatedAt: string;
}
