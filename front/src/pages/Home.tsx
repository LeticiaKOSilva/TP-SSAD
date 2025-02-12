import { useEffect, useState } from 'react';
import ProductCard from '../components/ProductCard';
import { DEFAULT_PRODUCT_IMAGE, Product } from '../types';

export default function Home() {
  const [products, setProducts] = useState<Product[]>([]);

  useEffect(() => {
    fetchProducts();
  }, []);

  const fetchProducts = async () => {
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
  };

  const getProducts = async () => {
    const resp = await fetch('http://localhost:8080/SuperDiaWebApi/rest/produto/', {
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
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-6">
        {products.map(product => (
          <ProductCard key={product.id} product={product} />
        ))}
      </div>
    </div>
  );
}