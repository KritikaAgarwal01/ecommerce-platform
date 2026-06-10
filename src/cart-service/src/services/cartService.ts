import { CosmosClient, Container } from '@azure/cosmos';
import { DefaultAzureCredential } from '@azure/identity';
import { Cart, CartItem } from '../models/cart';
import { v4 as uuidv4 } from 'uuid';

export class CartService {
  private container: Container;

  constructor() {
    const endpoint = process.env.COSMOS_DB_ENDPOINT!;
    const credential = new DefaultAzureCredential();
    const client = new CosmosClient({ endpoint, aadCredentials: credential });
    const database = client.database(process.env.COSMOS_DB_DATABASE || 'ecommerce');
    this.container = database.container(process.env.COSMOS_DB_CONTAINER || 'carts');
  }

  async getCart(userId: string): Promise<Cart | null> {
    try {
      const { resource } = await this.container.item(userId, userId).read<Cart>();
      return resource || null;
    } catch {
      return null;
    }
  }

  async addItem(userId: string, item: Omit<CartItem, 'addedAt'>): Promise<Cart> {
    let cart = await this.getCart(userId);

    const newItem: CartItem = {
      ...item,
      addedAt: new Date().toISOString()
    };

    if (!cart) {
      cart = {
        id: userId,
        userId,
        items: [newItem],
        totalPrice: item.quantity * item.unitPrice,
        updatedAt: new Date().toISOString()
      };
    } else {
      const existingIndex = cart.items.findIndex(i => i.productId === item.productId);
      if (existingIndex >= 0) {
        cart.items[existingIndex].quantity += item.quantity;
      } else {
        cart.items.push(newItem);
      }
      cart.totalPrice = cart.items.reduce((sum, i) => sum + i.quantity * i.unitPrice, 0);
      cart.updatedAt = new Date().toISOString();
    }

    await this.container.items.upsert(cart);
    return cart;
  }

  async updateItemQuantity(userId: string, productId: string, quantity: number): Promise<Cart | null> {
    const cart = await this.getCart(userId);
    if (!cart) return null;

    if (quantity === 0) {
      cart.items = cart.items.filter(i => i.productId !== productId);
    } else {
      const item = cart.items.find(i => i.productId === productId);
      if (!item) return null;
      item.quantity = quantity;
    }

    cart.totalPrice = cart.items.reduce((sum, i) => sum + i.quantity * i.unitPrice, 0);
    cart.updatedAt = new Date().toISOString();

    await this.container.items.upsert(cart);
    return cart;
  }

  async clearCart(userId: string): Promise<void> {
    try {
      await this.container.item(userId, userId).delete();
    } catch {
      // Cart may not exist — no-op
    }
  }
}
