import { Link } from 'react-router-dom';
import { CheckCircle } from 'lucide-react';

export default function CheckoutSuccess() {
  return (
    <div className="min-h-screen pt-24 pb-12 flex flex-col items-center justify-center">
      <div className="text-center">
        <CheckCircle className="mx-auto h-16 w-16 text-green-500" />
        <h2 className="mt-6 text-3xl font-bold text-gray-900">
          Pedido Confirmado!
        </h2>
        <p className="mt-2 text-lg text-gray-600">
          Obrigado por sua compra. Você receberá um email com os detalhes do pedido.
        </p>
        <div className="mt-8">
          <Link
            to="/"
            className="inline-flex items-center px-6 py-3 border border-transparent text-base font-medium rounded-md text-white bg-indigo-600 hover:bg-indigo-700"
          >
            Continuar Comprando
          </Link>
        </div>
      </div>
    </div>
  );
}