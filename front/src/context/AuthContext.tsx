import { createContext, ReactNode, useEffect, useState } from 'react';
import { User, AuthContextType } from '../types';
import useCart from './useCartContext';

export const AuthContext = createContext<AuthContextType | undefined>(undefined);

export default function AuthProvider({ children }: { children: ReactNode }) {
  // Lê o usuário armazenado no localStorage (se houver)
  const [user, setUser] = useState<User | null>(() => {
    const storedUser = localStorage.getItem('user');
    return storedUser ? JSON.parse(storedUser) : null;
  });
  const { setCart } = useCart();

  // Salva o usuário no localStorage quando uma alteração ocorrer
  useEffect(() => {
    if (user) {
      localStorage.setItem('user', JSON.stringify(user));
      if (user.perfil === 'cliente') {
        fetchCarrinho(user.id!);
      }
    } else {
      localStorage.removeItem('user');
      setCart([]); // Clear cart on logout
    }
  }, [user]);

  const login = async (login: string, senha: string) => {
    try {
      try {
        const resp = await fetch('/api/usuario/authenticate', {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
          },
          body: JSON.stringify({
            login,
            senha
          }),
        });
    
        if (!resp.ok) {
          const errorMessage = await resp.text();
          throw new Error(errorMessage);
        }
    
        const data = await resp.json();
        setUser(data);
        return data;
      } catch (error) {
        console.error(error)
        throw error
      }
    } catch (error) {
      console.error(error);
      throw error;
    }
  };

  const fetchCarrinho = async (usuarioId: string) => {
    try {
      const resp = await fetch(`/api/carrinho/${usuarioId}`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ login: user?.pessoa.email, senha: user?.senha }),
      });

      if (!resp.ok) throw new Error('Erro ao buscar carrinho');

      if (resp.status === 204) {
        return;
      }

      const carrinho = await resp.json();
      setCart(carrinho?.itens || []); // Set cart items in CartContext
    } catch (error) {
      console.error('Erro ao buscar carrinho:', error);
    }
  };

  const logout = () => {
    setUser(null);
  };

  return (
    <AuthContext.Provider value={{ user, login, logout }}>
      {children}
    </AuthContext.Provider>
  );
}