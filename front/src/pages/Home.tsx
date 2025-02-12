import ProductCard from '../components/ProductCard';
import { Product } from '../types';

// Mock products data
const products: Product[] = [
  {
    id: '1',
    name: 'Smartphone XYZ',
    description: 'Um smartphone incrível com câmera de alta resolução',
    price: 1999.99,
    imageUrl: 'https://images.unsplash.com/photo-1511707171634-5f897ff02aa9?auto=format&fit=crop&q=80&w=800',
    stock: 10
  },
  {
    id: '2',
    name: 'Notebook Pro',
    description: 'Notebook potente para todas as suas necessidades',
    price: 4999.99,
    imageUrl: 'https://images.unsplash.com/photo-1496181133206-80ce9b88a853?auto=format&fit=crop&q=80&w=800',
    stock: 5
  },
  // Add more mock products as needed
];

export default function Home() {
  return (
    <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 pt-24">
      <h1 className="text-3xl font-bold text-gray-900 mb-8">Produtos em Destaque</h1>
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-6">
        {products.map(product => (
          <ProductCard key={product.id} product={product} />
        ))}
      </div>
    </div>
  );
}