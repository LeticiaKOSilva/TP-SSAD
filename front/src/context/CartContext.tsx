import { createContext, ReactNode, useState } from 'react';
import { CartContextType, CartItem, Product } from '../types';

export const CartContext = createContext<CartContextType | undefined>(undefined);

export default function CartProvider({ children }: { children: ReactNode }) {
  const [items, setItems] = useState<CartItem[]>([]);

  const addToCart = (product: Product) => {
    setItems(currentItems => {
      const existingItem = currentItems.find(item => item.id === product.id);
      if (existingItem) {
        return currentItems.map(item =>
          item.id === product.id
            ? { ...item, quantidade: item.quantidade + 1 }
            : item
        );
      }
      return [...currentItems, { ...product, quantidade: 1 }];
    });
  };

  const removeFromCart = (productId: string) => {
    setItems(currentItems => currentItems.filter(item => item.id !== productId));
  };

  const updateQuantity = (productId: string, quantity: number) => {
    if (quantity < 1) return;
    setItems(currentItems =>
      currentItems.map(item =>
        item.id === productId ? { ...item, quantity } : item
      )
    );
  };

  const clearCart = () => {
    setItems([]);
  };

  const setCart = (cartItems: CartItem[]) => {
    setItems(cartItems);
  };

  return (
    <CartContext.Provider
      value={{ items, addToCart, removeFromCart, updateQuantity, clearCart, setCart }}
    >
      {children}
    </CartContext.Provider>
  );
}