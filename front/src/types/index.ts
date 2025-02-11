export interface Product {
  id: string;
  name: string;
  description: string;
  price: number;
  imageUrl: string;
  stock: number;
}

export interface CartItem extends Product {
  quantity: number;
}

export interface Person {
  id?: string;
  nome: string;
  email: string;
  endereco: string;
  cpf: string;
  telefone: string;
  dataNascimento: Date;
}

export interface User {
  id?: string;
  email: string;
  perfil: 'admin' | 'cliente';
  senha: string;
  pessoa: Person;
}

export interface AuthContextType {
  user: User | null;
  login: (email: string, password: string) => Promise<User>;
  logout: () => void;
}

export interface CartContextType {
  items: CartItem[];
  addToCart: (product: Product) => void;
  removeFromCart: (productId: string) => void;
  updateQuantity: (productId: string, quantity: number) => void;
  clearCart: () => void;
}