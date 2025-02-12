import useCart from '../context/useCartContext';
import { Product } from '../types';
import { ShoppingCart } from 'lucide-react';

interface ProductCardProps {
  product: Product;
}

export default function ProductCard({ product }: ProductCardProps) {
  const { addToCart } = useCart();

  return (
    <div className="bg-white rounded-lg shadow-md overflow-hidden flex flex-col">
      <img
        src={product.urlImagem}
        alt={product.nome}
        className="w-full h-48 object-cover"
      />
      <div className="flex flex-col justify-between p-4 flex-grow">
        <div>
          <h3 className="text-lg font-semibold text-gray-900">{product.nome}</h3>
          <p className="mt-1 text-gray-500 text-sm">{product.descricao}</p>
        </div>
        <div className="mt-4 flex items-center justify-between">
          <span className="text-xl font-bold text-gray-900">
            R$ {product.preco.toFixed(2)}
          </span>
          <button
            onClick={() => addToCart(product)}
            className="inline-flex items-center px-3 py-2 border border-transparent text-sm font-medium rounded-md text-white bg-indigo-600 hover:bg-indigo-700"
          >
            <ShoppingCart className="h-4 w-4 mr-2" />
            Adicionar
          </button>
        </div>
      </div>
    </div>
  );
}