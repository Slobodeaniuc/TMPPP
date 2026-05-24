import { BrowserRouter, Routes, Route } from 'react-router-dom';
import { AuthProvider } from './context/AuthContext';
import { ToastProvider } from './components/Toast';
import PrivateRoute from './components/PrivateRoute';
import Navbar from './components/Navbar';
import LoginPage from './pages/LoginPage';
import CatalogPage from './pages/CatalogPage';
import LoansPage from './pages/LoansPage';
import CartPage from './pages/CartPage';
import MembersPage from './pages/MembersPage';

export default function App() {
  return (
    <BrowserRouter>
      <AuthProvider>
        <ToastProvider>
          <Routes>
            <Route path="/login" element={<LoginPage />} />
            <Route path="/*" element={
              <PrivateRoute>
                <div className="min-h-screen bg-gray-100">
                  <Navbar />
                  <main className="container mx-auto px-4 py-8">
                    <Routes>
                      <Route path="/" element={<CatalogPage />} />
                      <Route path="/loans" element={<LoansPage />} />
                      <Route path="/cart" element={<CartPage />} />
                      <Route path="/members" element={<MembersPage />} />
                    </Routes>
                  </main>
                </div>
              </PrivateRoute>
            } />
          </Routes>
        </ToastProvider>
      </AuthProvider>
    </BrowserRouter>
  );
}
