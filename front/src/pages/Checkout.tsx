import { ChangeEvent, FormEvent, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { CreditCard } from 'lucide-react';
import useCart from '../context/useCartContext';
import useAuth from '../context/useAuthContext';
import InputMask from 'react-input-mask';

interface CheckoutForm {
  name: string;
  email: string;
  cardNumber: string;
  cardExpiry: string;
  cardCvc: string;
}

export default function Checkout() {
  const navigate = useNavigate();
  const { items, clearCart, turnCartIntoNotaFiscal } = useCart();
  const { user } = useAuth();

  const [formData, setFormData] = useState<CheckoutForm>({
    name: user?.pessoa.nome || '',
    email: user?.pessoa.email || '',
    cardNumber: '',
    cardExpiry: '',
    cardCvc: '',
  });
  const [loading, setLoading] = useState(false);

  const total = items.reduce((sum, item) => sum + item.produto.preco * item.quantidade, 0);

  const handleSubmit = async (e: FormEvent) => {
    e.preventDefault();
    setLoading(true);

    try {
      await turnCartIntoNotaFiscal();
      clearCart();
      navigate('/checkout/success');
    } catch (error) {
      console.error(error);
      alert('Erro ao criar nota fiscal');
    } finally {
      setLoading(false);
    }
  };

  const handleInputChange = (e: ChangeEvent<HTMLInputElement>) => {
    const { name, value } = e.target;
    setFormData(prev => ({ ...prev, [name]: value }));
  };

  return (
    <div className="min-h-screen pt-24 pb-12">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="grid grid-cols-1 md:grid-cols-2 gap-8">
          <div>
            <h2 className="text-2xl font-bold text-gray-900 mb-6">
              Informações de Entrega
            </h2>
            <form onSubmit={handleSubmit} className="space-y-6">
              <div>
                <label className="block text-sm font-medium text-gray-700">
                  Nome Completo
                </label>
                <input
                  type="text"
                  name="name"
                  required
                  value={formData.name}
                  onChange={handleInputChange}
                  className="mt-1 block w-full rounded-md border border-gray-300 px-3 py-2"
                />
              </div>
              <div>
                <label className="block text-sm font-medium text-gray-700">
                  Email
                </label>
                <input
                  type="email"
                  name="email"
                  required
                  value={formData.email}
                  onChange={handleInputChange}
                  className="mt-1 block w-full rounded-md border border-gray-300 px-3 py-2"
                />
              </div>
              <div className="pt-8">
                <h2 className="text-2xl font-bold text-gray-900 mb-6">
                  Informações de Pagamento
                </h2>
                <div className="space-y-6">
                  <div>
                    <label className="block text-sm font-medium text-gray-700">
                      Número do Cartão
                    </label>
                    <div className="mt-1 relative">
                       <InputMask
                          type="text"
                          name="cardNumber"
                          mask="9999 9999 9999 9999"
                          value={formData.cardNumber}
                          onChange={handleInputChange}
                          maskChar={null}
                          required
                          className="block w-full rounded-md border border-gray-300 px-3 py-2 pl-10"
                        />
                      <CreditCard className="absolute left-3 top-2.5 h-5 w-5 text-gray-400" />
                    </div>
                  </div>
                  <div className="grid grid-cols-2 gap-4">
                    <div>
                      <label className="block text-sm font-medium text-gray-700">
                        Validade
                      </label>
                      <InputMask
                        type="text"
                        name="cardExpiry"
                        placeholder="MM/AA"
                        mask="99/99"
                        required
                        value={formData.cardExpiry}
                        onChange={handleInputChange}
                        maskChar={null}
                        className="mt-1 block w-full rounded-md border border-gray-300 px-3 py-2"
                      />
                    </div>
                    <div>
                      <label className="block text-sm font-medium text-gray-700">
                        CVC
                      </label>
                      <InputMask
                        type="text"
                        name="cardCvc"
                        mask="999"
                        required
                        value={formData.cardCvc}
                        onChange={handleInputChange}
                        maskChar={null}
                        className="mt-1 block w-full rounded-md border border-gray-300 px-3 py-2"
                      />
                    </div>
                  </div>
                </div>
              </div>

              <button
                type="submit"
                disabled={loading}
                className="w-full flex justify-center py-3 px-4 border border-transparent rounded-md shadow-sm text-sm font-medium text-white bg-indigo-600 hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500 disabled:opacity-50"
              >
                {loading ? 'Processando...' : 'Finalizar Compra'}
              </button>
            </form>
          </div>

          <div>
            <div className="bg-gray-50 rounded-lg p-6 sticky top-24">
              <h2 className="text-lg font-medium text-gray-900 mb-6">
                Resumo do Pedido
              </h2>
              <div className="space-y-4">
                {items.map(item => (
                  <div key={item.id} className="flex justify-between">
                    <div className="flex items-center">
                      <img
                        src={item.produto.urlImagem}
                        alt={item.produto.nome}
                        className="h-16 w-16 object-cover rounded"
                      />
                      <div className="ml-4">
                        <p className="text-sm font-medium text-gray-900">
                          {item.produto.nome}
                        </p>
                        <p className="text-sm text-gray-500">
                          Quantidade: {item.quantidade}
                        </p>
                      </div>
                    </div>
                    <p className="text-sm font-medium text-gray-900">
                      R$ {(item.produto.preco * item.quantidade).toFixed(2)}
                    </p>
                  </div>
                ))}
              </div>
              <div className="flex justify-between mt-6 border-t border-gray-200 pt-6">
                <p className="text-base font-medium text-gray-900">Total</p>
                <p className="text-base font-medium text-gray-900">
                  R$ {total.toFixed(2)}
                </p>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}