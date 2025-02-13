export const DEFAULT_PRODUCT_IMAGE = 'https://images.pexels.com/photos/9070110/pexels-photo-9070110.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=1';

export interface Product {
  id?: string;
  nome: string;
  descricao: string;
  preco: number;
  estoqueMinimo: number;
  quantidadeEstoque: number;
  urlImagem?: string;
}

export interface CartItem extends Product {
  quantidade: number;
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
  setCart: (cartItems: CartItem[]) => void;
}