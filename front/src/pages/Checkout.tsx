import { ChangeEvent, FormEvent, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { CreditCard, Truck } from 'lucide-react';
import useCart from '../context/useCartContext';

interface CheckoutForm {
  name: string;
  email: string;
  address: string;
  city: string;
  state: string;
  zip: string;
  cardNumber: string;
  cardExpiry: string;
  cardCvc: string;
}

export default function Checkout() {
  const navigate = useNavigate();
  const { items, clearCart } = useCart();
  const [formData, setFormData] = useState<CheckoutForm>({
    name: '',
    email: '',
    address: '',
    city: '',
    state: '',
    zip: '',
    cardNumber: '',
    cardExpiry: '',
    cardCvc: '',
  });
  const [loading, setLoading] = useState(false);

  const total = items.reduce((sum, item) => sum + item.preco * item.quantidade, 0);
  const shipping = 15.0;
  const finalTotal = total + shipping;

  const handleSubmit = async (e: FormEvent) => {
    e.preventDefault();
    setLoading(true);

    try {
      // Simulate payment processing
      await new Promise(resolve => setTimeout(resolve, 2000));
      
      // Clear cart and redirect to success page
      clearCart();
      navigate('/checkout/success');
    } catch (error) {
      console.error('Checkout failed:', error);
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
              <div>
                <label className="block text-sm font-medium text-gray-700">
                  Endereço
                </label>
                <input
                  type="text"
                  name="address"
                  required
                  value={formData.address}
                  onChange={handleInputChange}
                  className="mt-1 block w-full rounded-md border border-gray-300 px-3 py-2"
                />
              </div>
              <div className="grid grid-cols-2 gap-4">
                <div>
                  <label className="block text-sm font-medium text-gray-700">
                    Cidade
                  </label>
                  <input
                    type="text"
                    name="city"
                    required
                    value={formData.city}
                    onChange={handleInputChange}
                    className="mt-1 block w-full rounded-md border border-gray-300 px-3 py-2"
                  />
                </div>
                <div>
                  <label className="block text-sm font-medium text-gray-700">
                    Estado
                  </label>
                  <input
                    type="text"
                    name="state"
                    required
                    value={formData.state}
                    onChange={handleInputChange}
                    className="mt-1 block w-full rounded-md border border-gray-300 px-3 py-2"
                  />
                </div>
              </div>
              <div>
                <label className="block text-sm font-medium text-gray-700">
                  CEP
                </label>
                <input
                  type="text"
                  name="zip"
                  required
                  value={formData.zip}
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
                      <input
                        type="text"
                        name="cardNumber"
                        required
                        value={formData.cardNumber}
                        onChange={handleInputChange}
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
                      <input
                        type="text"
                        name="cardExpiry"
                        placeholder="MM/AA"
                        required
                        value={formData.cardExpiry}
                        onChange={handleInputChange}
                        className="mt-1 block w-full rounded-md border border-gray-300 px-3 py-2"
                      />
                    </div>
                    <div>
                      <label className="block text-sm font-medium text-gray-700">
                        CVC
                      </label>
                      <input
                        type="text"
                        name="cardCvc"
                        required
                        value={formData.cardCvc}
                        onChange={handleInputChange}
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
                        src={item.imageUrl}
                        alt={item.nome}
                        className="h-16 w-16 object-cover rounded"
                      />
                      <div className="ml-4">
                        <p className="text-sm font-medium text-gray-900">
                          {item.nome}
                        </p>
                        <p className="text-sm text-gray-500">
                          Quantidade: {item.quantidade}
                        </p>
                      </div>
                    </div>
                    <p className="text-sm font-medium text-gray-900">
                      R$ {(item.preco * item.quantidade).toFixed(2)}
                    </p>
                  </div>
                ))}
              </div>
              <div className="mt-6 border-t border-gray-200 pt-6">
                <div className="flex justify-between">
                  <p className="text-sm text-gray-600">Subtotal</p>
                  <p className="text-sm font-medium text-gray-900">
                    R$ {total.toFixed(2)}
                  </p>
                </div>
                <div className="flex justify-between mt-2">
                  <div className="flex items-center">
                    <Truck className="h-5 w-5 text-gray-400 mr-2" />
                    <p className="text-sm text-gray-600">Frete</p>
                  </div>
                  <p className="text-sm font-medium text-gray-900">
                    R$ {shipping.toFixed(2)}
                  </p>
                </div>
                <div className="flex justify-between mt-6 border-t border-gray-200 pt-6">
                  <p className="text-base font-medium text-gray-900">Total</p>
                  <p className="text-base font-medium text-gray-900">
                    R$ {finalTotal.toFixed(2)}
                  </p>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}