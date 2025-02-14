import { useEffect, useState } from 'react';
import LoginModal from '../components/LoginModal';
import { Minus, Plus, Trash2 } from 'lucide-react';
import useCart from '../context/useCartContext';
import useAuth from '../context/useAuthContext';
import { useNavigate } from 'react-router-dom';

export default function Cart() {
  const { items, removeFromCart, updateQuantity } = useCart();
  const { user } = useAuth();
  const [showLoginModal, setShowLoginModal] = useState(false);
  const { fetchCarrinho } = useCart();
  const navigate = useNavigate();

  const total = items.reduce((sum, item) => sum + item.produto.preco * item.quantidade, 0);

  const handleCheckout = () => {
    if (!user) {
      setShowLoginModal(true);
    } else {
      navigate('/checkout');
    }
  };

  useEffect(() => {
    if (user) {
      fetchCarrinho();
    }
  }, [])

  return (
    <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 pt-24">
      <h1 className="text-3xl font-bold text-gray-900 mb-8">Carrinho</h1>
      
      {items.length === 0 ? (
        <p className="text-gray-500">Seu carrinho está vazio.</p>
      ) : (
        <>
          <div className="md:hidden space-y-4">
            {items.map(item => (
              <div key={item.id} className="bg-white p-4 rounded-lg shadow-sm">
                <div className="flex justify-between items-start">
                  <div className="flex items-center gap-4">
                    <img
                      src={item.produto.urlImagem}
                      alt={item.produto.nome}
                      className="h-16 w-16 object-cover rounded"
                    />
                    <div>
                      <div className="font-medium text-gray-900">{item.produto.nome}</div>
                      <div className="text-sm text-gray-500 mt-1">
                        R$ {item.produto.preco.toFixed(2)} × {item.quantidade}
                      </div>
                      <div className="text-sm font-medium text-gray-900 mt-1">
                        R$ {(item.produto.preco * item.quantidade).toFixed(2)}
                      </div>
                    </div>
                  </div>
                  <button
                    onClick={() => removeFromCart(item.id!)}
                    className="text-red-600 hover:text-red-900"
                  >
                    <Trash2 className="h-5 w-5" />
                  </button>
                </div>
                <div className="flex items-center justify-between mt-4">
                  <div className="flex items-center space-x-2">
                    <button
                      onClick={() => updateQuantity(item.id!, item.quantidade - 1)}
                      className="p-1 rounded-md hover:bg-gray-100"
                    >
                      <Minus className="h-4 w-4" />
                    </button>
                    <span className="text-sm text-gray-900">{item.quantidade}</span>
                    <button
                      onClick={() => updateQuantity(item.id!, item.quantidade + 1)}
                      className="p-1 rounded-md hover:bg-gray-100"
                    >
                      <Plus className="h-4 w-4" />
                    </button>
                  </div>
                </div>
              </div>
            ))}
          </div>
          <div className="hidden md:block bg-white rounded-lg shadow-sm overflow-y-auto sm:overflow-hidden">
            <table className="min-w-full divide-y divide-gray-200">
              <thead className="bg-gray-50">
                <tr>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                    Produto
                  </th>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                    Preço
                  </th>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                    Quantidade
                  </th>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                    Subtotal
                  </th>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                    Ações
                  </th>
                </tr>
              </thead>
              <tbody className="bg-white divide-y divide-gray-200">
                {items.map(item => (
                  <tr key={item.id}>
                    <td className="px-6 py-4 whitespace-nowrap">
                      <div className="flex items-center">
                        <img
                          src={item.produto.urlImagem}
                          alt={item.produto.nome}
                          className="h-16 w-16 object-cover rounded"
                        />
                        <div className="ml-4">
                          <div className="text-sm font-medium text-gray-900">
                            {item.produto.nome}
                          </div>
                        </div>
                      </div>
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap">
                      <div className="text-sm text-gray-900">
                        R$ {item.produto.preco.toFixed(2)}
                      </div>
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap">
                      <div className="flex items-center space-x-2">
                        <button
                          onClick={() => updateQuantity(item.id!, item.quantidade - 1)}
                          className="p-1 rounded-md hover:bg-gray-100"
                        >
                          <Minus className="h-4 w-4" />
                        </button>
                        <span className="text-sm text-gray-900">{item.quantidade}</span>
                        <button
                          onClick={() => updateQuantity(item.id!, item.quantidade + 1)}
                          className="p-1 rounded-md hover:bg-gray-100"
                        >
                          <Plus className="h-4 w-4" />
                        </button>
                      </div>
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap">
                      <div className="text-sm text-gray-900">
                        R$ {(item.produto.preco * item.quantidade).toFixed(2)}
                      </div>
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap">
                      <button
                        onClick={() => removeFromCart(item.id!)}
                        className="text-red-600 hover:text-red-900"
                      >
                        <Trash2 className="h-5 w-5" />
                      </button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>

          <div className="mt-8 bg-white rounded-lg shadow p-6">
            <div className="flex justify-between items-center mb-4">
              <span className="text-lg font-medium text-gray-900">Total</span>
              <span className="text-2xl font-bold text-gray-900">
                R$ {total.toFixed(2)}
              </span>
            </div>
            <button
              onClick={handleCheckout}
              className="w-full bg-indigo-600 text-white py-3 px-4 rounded-md hover:bg-indigo-700 transition-colors"
            >
              Finalizar Compra
            </button>
          </div>
        </>
      )}

      <LoginModal
        isOpen={showLoginModal}
        onClose={() => setShowLoginModal(false)}
        onSuccess={() => navigate('/checkout')}
      />
    </div>
  );
}