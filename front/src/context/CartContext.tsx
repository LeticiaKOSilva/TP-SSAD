import { createContext, ReactNode, useEffect, useState } from 'react';
import { CartContextType, CartItem, Product } from '../types';
import useAuth from './useAuthContext';

export const CartContext = createContext<CartContextType | undefined>(undefined);

export default function CartProvider({ children }: { children: ReactNode }) {
  const { user } = useAuth();
  const [id, setId] = useState<string>(() => {
    if (!user) {
      const storedId = localStorage.getItem('cartId');
      return storedId || '';
    }

    return '';
  });
  const [items, setItems] = useState<CartItem[]>(() => {
    if (!user) {
      const storedItems = localStorage.getItem('cartItems');
      return storedItems ? JSON.parse(storedItems) : [];
    }

    return [];
  });

  useEffect(() => {
    console.log(123)
    if (user?.perfil === 'cliente') {
      fetchCarrinho();
    }
  }, [user]);

  useEffect(() => {
    if (!user) {
      localStorage.setItem('cartId', id);
      localStorage.setItem('cartItems', JSON.stringify(items));
    }
  }, [id, items]);

  const fetchCarrinho = async () => {
    console.log({user})
    if (!user) return;

    try {
      const resp = await fetch(`/api/carrinho/${user.id}`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ login: user.pessoa.email, senha: user.senha }),
      });

      if (!resp.ok) throw new Error('Erro ao buscar carrinho');

      if (resp.status === 204 && !items.length) {
        return;
      }

      const carrinho = await resp.json();
      console.log({carrinho})

      if (!carrinho && id) {
        await updateCart({ id, cliente: user, itens: items });
        return;
      }

      const  auxItems = carrinho.itens?.length ? carrinho.itens :  items || []

      setId(carrinho.id);
      setCart(auxItems);
    } catch (error) {
      console.error('Erro ao buscar carrinho:', error);
    }
  };

  const addToCart = async (product: Product) => {
    let currentId = id
    if (!id) {
      try {
        const newCarrinho = await createCart({ cliente: user, itens: [] });
        setId(newCarrinho.id);
        currentId = newCarrinho.id
      } catch (error) {
        console.error(error);
        return alert('Erro ao criar carrinho');
      }
    }
    
    const itemIndex = items.findIndex(item => item.produto.id === product.id);

    if (itemIndex !== -1) {
      const updatedItems = [...items];
      updatedItems[itemIndex] = {
        ...updatedItems[itemIndex],
        quantidade: updatedItems[itemIndex].quantidade + 1,
      };

      await updateItemQuantity(updatedItems[itemIndex].id, updatedItems[itemIndex].quantidade);

      await updateCart({ id: currentId, cliente: user, itens: [updatedItems.map(item => { return { ...item, valorTotal: undefined } })] });

      return setItems(updatedItems);
    }

    const newProduct = { id: "", produto: product, quantidade: 1, valorUnitario: product.preco };
    
    try {
      const createdItem = await createItem({ produto: product, quantidade: 1, valorUnitario: product.preco });
      newProduct.id = createdItem.id;
    } catch(error) {
      console.error('Erro ao criar item:', error);
      return alert('Erro ao criar item');
    }

    await updateCart({ id: currentId, cliente: user, itens: [...items.map(item => { return { ...item, valorTotal: undefined } }), newProduct] });

    setItems(currentItems => {
      return [...currentItems, newProduct];
    });
  };

  const removeFromCart = async (itemId: string) => {
    const newItems = items.filter(item => item.id !== itemId)
    setItems(newItems);

    await removeItem(itemId);
  };

  const updateQuantity = async (itemId: string, quantity: number) => {
    if (quantity < 1) return;

    setItems(currentItems =>
      currentItems.map(item =>
        item.id === itemId ? { ...item, quantidade: quantity } : item
      )
    );

    await updateItemQuantity(itemId, quantity);
  };

  const clearCart = () => {
    setItems([]);
  };

  const setCart = (cartItems: CartItem[]) => {
    setItems(cartItems);
  };

  const createItem = async (itemData) => {
    const resp = await fetch('/api/item/create', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        login: user?.pessoa.email,
        senha: user?.senha,
        item: itemData,
      }),
    });
    if (!resp.ok) throw new Error('Erro ao criar item');
    return await resp.json();
  };

  const createCart = async (cartData) => {
    const resp = await fetch('/api/carrinho/create', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        login: user?.pessoa.email,
        senha: user?.senha,
        carrinho: cartData,
      }),
    });
    if (!resp.ok) throw new Error('Erro ao criar item');
    return await resp.json();
  };

  const updateCart = async (cartData) => {
    const resp = await fetch('/api/carrinho/update', {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        login: user?.pessoa.email,
        senha: user?.senha,
        carrinho: cartData,
      }),
    });
    if (!resp.ok) throw new Error('Erro ao atualizar carrinho');
    return await resp.json();
  };

  const updateItemQuantity = async (itemId, newQuantity) => {
    const resp = await fetch('/api/item/update', {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        login: user?.pessoa.email,
        senha: user?.senha,
        item: { id: itemId, quantidade: newQuantity },
      }),
    });
    if (!resp.ok) throw new Error('Erro ao atualizar item');
    return await resp.json();
  };

  const removeItem = async (itemId) => {
    const resp = await fetch('/api/item/delete', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        login: user?.pessoa.email,
        senha: user?.senha,
        item: { id: itemId },
      }),
    });
    if (!resp.ok) throw new Error('Erro ao remover item');
    return await resp.text();
  };

  const deleteCart = async () => {
    const resp = await fetch(`/api/carrinho/delete/${user?.id}`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        login: user?.pessoa.email,
        senha: user?.senha,
      }),
    });
    if (!resp.ok) throw new Error('Erro ao limpar carrinho');
    return await resp.json();
  };

  return (
    <CartContext.Provider
      value={{ items, fetchCarrinho, addToCart, removeFromCart, updateQuantity, clearCart, setCart }}
    >
      {children}
    </CartContext.Provider>
  );
}