import { useEffect, useState } from 'react';
import ProductCard from '../components/ProductCard';
import { DEFAULT_PRODUCT_IMAGE, Product } from '../types';
import { Loader2 } from 'lucide-react';

export default function Home() {
  const [products, setProducts] = useState<Product[]>([]);
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    fetchProducts();
  }, []);

  const fetchProducts = async () => {
    setLoading(true);

    try {
      const products = await getProducts();
      setProducts(
        products.map((product) => ({
          ...product,
          urlImagem:
            product.urlImagem ||
            DEFAULT_PRODUCT_IMAGE,
        })) as Product[]
      );
    } catch (error) {
      console.error(error);
      alert('Erro ao buscar produtos');
    }

    setLoading(false);
  };

  const getProducts = async () => {
    const resp = await fetch('/api/produto/', {
      method: 'GET',
      headers: {
        'Content-Type': 'application/json',
      },
    });

    if (!resp.ok) {
      const error = await resp.text();
      throw new Error(error);
    }
  
    return await resp.json();
  }

  return (
    <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 pt-24">
      <h1 className="text-3xl font-bold text-gray-900 mb-8">Produtos</h1>
      {
        loading ? (
          <Loader2 className="mx-auto h-16 w-16 text-indigo-600 animate-spin m-5" />
        ) : products.length === 0 ? (
          <p className="text-gray-500">Nenhum produto encontrado.</p>
        ) :
        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-6">
          {products.map(product => (
            <ProductCard key={product.id} product={product} />
          ))}
        </div>
      }
    </div>
  );
}