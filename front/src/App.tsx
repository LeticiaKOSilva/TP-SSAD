import { ReactNode } from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate, useLocation } from 'react-router-dom';
import Header from './components/Header';
import Home from './pages/Home';
import Cart from './pages/Cart';
import Register from './pages/Register';
import Login from './pages/Login';
import Checkout from './pages/Checkout';
import CheckoutSuccess from './pages/CheckoutSuccess';
import Dashboard from './pages/Admin/Dashboard';
import AuthProvider from './context/AuthContext';
import CartProvider from './context/CartContext';
import useAuth from './context/useAuthContext';

function ProtectedRoute({ children, adminOnly = false }: { children: ReactNode, adminOnly?: boolean }) {
  const { user } = useAuth();

  if (!user) {
    return <Navigate to="/" replace />;
  }

  if (adminOnly && user.perfil !== 'admin') {
    return <Navigate to="/" replace />;
  }

  return <>{children}</>;
}

function AdminRedirect() {
  const { user } = useAuth();
  const location = useLocation();

  // Redireciona para a página de admin se o usuário for admin
  if (user?.perfil === 'admin' && (location.pathname === '/' || location.pathname === '/cart')) {
    return <Navigate to="/admin" replace />;
  }

  return null;
}

function App() {
  return (
    <CartProvider>
      <AuthProvider>
        <Router future={{ v7_startTransition: true, v7_relativeSplatPath: true }}>
          <div className="min-h-screen bg-gray-100">
            <Header />
            <AdminRedirect />
            <Routes>
              <Route path="/" element={<Home />} />
              <Route path="/cart" element={<Cart />} />
              <Route path="/register" element={<Register />} />
              <Route path="/login" element={<Login />} />
              <Route 
                path="/checkout" 
                element={
                  <ProtectedRoute>
                    <Checkout />
                  </ProtectedRoute>
                } 
              />
              <Route path="/checkout/success" element={<CheckoutSuccess />} />
              <Route
                path="/admin"
                element={
                  <ProtectedRoute adminOnly>
                    <Dashboard />
                  </ProtectedRoute>
                }
              />
            </Routes>
          </div>
        </Router>
      </AuthProvider>
    </CartProvider>
  );
}

export default App;