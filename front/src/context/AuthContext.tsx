import { createContext, ReactNode, useState } from 'react';
import { User, AuthContextType } from '../types';

export const AuthContext = createContext<AuthContextType | undefined>(undefined);

export default function AuthProvider({ children }: { children: ReactNode }) {
  const [user, setUser] = useState<User | null>(null);

  const login = async (login: string, senha: string) => {
    try {
      try {
        const resp = await fetch('http://localhost:8080/SuperDiaWebApi/rest/usuario/authenticate', {
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

  const logout = () => {
    setUser(null);
  };

  return (
    <AuthContext.Provider value={{ user, login, logout }}>
      {children}
    </AuthContext.Provider>
  );
}