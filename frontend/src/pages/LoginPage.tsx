import { useState, FormEvent } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

const DEMO_USERS = [
  { name: 'Alex Popescu',       email: 'alex.popescu@library.ro' },
  { name: 'Maria Ionescu',      email: 'maria.ionescu@library.ro' },
  { name: 'Andrei Constantin',  email: 'andrei.constantin@library.ro' },
  { name: 'Elena Dumitrescu',   email: 'elena.dumitrescu@library.ro' },
  { name: 'Mihai Popa',         email: 'mihai.popa@library.ro' },
  { name: 'Cristina Stan',      email: 'cristina.stan@library.ro' },
];

export default function LoginPage() {
  const { login } = useAuth();
  const navigate = useNavigate();
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  async function handleSubmit(e: FormEvent) {
    e.preventDefault();
    setError('');
    setLoading(true);
    try {
      await login(username, password);
      navigate('/', { replace: true });
    } catch {
      setError('Email sau parolă incorectă. Încearcă din nou.');
    } finally {
      setLoading(false);
    }
  }

  function fillUser(email: string) {
    setUsername(email);
    setPassword('parola123');
    setError('');
  }

  return (
    <div className="min-h-screen flex">
      {/* Left panel */}
      <div className="hidden lg:flex lg:w-1/2 bg-gray-900 text-white flex-col justify-between p-12">
        <div>
          <div className="flex items-center gap-3 mb-12">
            <span className="text-3xl">📚</span>
            <span className="text-xl font-bold tracking-tight">LibraryMS</span>
          </div>
          <h1 className="text-4xl font-bold leading-tight mb-4">
            Sistemul de management<br />al bibliotecii
          </h1>
          <p className="text-gray-400 text-lg">
            Împrumută cărți, reviste și DVD-uri. Gestionează-ți împrumuturile și coșul de cumpărături.
          </p>
        </div>

        <div>
          <p className="text-gray-500 text-sm mb-4 uppercase tracking-wider font-semibold">Conturi demo</p>
          <div className="grid grid-cols-2 gap-2">
            {DEMO_USERS.map(u => (
              <button
                key={u.email}
                onClick={() => fillUser(u.email)}
                className="text-left p-3 rounded-lg bg-gray-800 hover:bg-gray-700 transition-colors"
              >
                <p className="text-sm font-medium text-white">{u.name}</p>
                <p className="text-xs text-gray-400 truncate">{u.email}</p>
              </button>
            ))}
          </div>
          <p className="text-gray-600 text-xs mt-3">Parola pentru toate conturile: <span className="text-gray-400 font-mono">parola123</span></p>
        </div>
      </div>

      {/* Right panel — form */}
      <div className="w-full lg:w-1/2 flex items-center justify-center p-8 bg-white">
        <div className="w-full max-w-md">
          <div className="lg:hidden flex items-center gap-2 mb-8">
            <span className="text-2xl">📚</span>
            <span className="text-lg font-bold text-gray-900">LibraryMS</span>
          </div>

          <h2 className="text-3xl font-bold text-gray-900 mb-2">Bun venit înapoi</h2>
          <p className="text-gray-500 mb-8">Intră în contul tău pentru a continua.</p>

          <form onSubmit={handleSubmit} className="space-y-5">
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1.5">
                Adresă email
              </label>
              <input
                type="email"
                value={username}
                onChange={e => setUsername(e.target.value)}
                placeholder="nume@library.ro"
                required
                className="w-full border border-gray-300 rounded-xl px-4 py-3 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent transition"
              />
            </div>

            <div>
              <div className="flex justify-between items-center mb-1.5">
                <label className="block text-sm font-medium text-gray-700">Parolă</label>
                <span className="text-xs text-blue-600 cursor-pointer hover:underline">Ai uitat parola?</span>
              </div>
              <input
                type="password"
                value={password}
                onChange={e => setPassword(e.target.value)}
                placeholder="••••••••"
                required
                className="w-full border border-gray-300 rounded-xl px-4 py-3 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent transition"
              />
            </div>

            {error && (
              <div className="bg-red-50 border border-red-200 text-red-700 rounded-xl px-4 py-3 text-sm">
                {error}
              </div>
            )}

            <button
              type="submit"
              disabled={loading}
              className="w-full bg-blue-600 text-white py-3 rounded-xl font-semibold text-sm hover:bg-blue-700 transition-colors disabled:opacity-60 flex items-center justify-center gap-2"
            >
              {loading && <div className="w-4 h-4 border-2 border-white border-t-transparent rounded-full animate-spin" />}
              {loading ? 'Se autentifică...' : 'Autentificare'}
            </button>
          </form>

          {/* Mobile demo users */}
          <div className="lg:hidden mt-8 pt-6 border-t border-gray-200">
            <p className="text-xs text-gray-400 mb-3 font-medium uppercase tracking-wider">Conturi demo (click pentru completare)</p>
            <div className="grid grid-cols-2 gap-2">
              {DEMO_USERS.slice(0, 4).map(u => (
                <button key={u.email} onClick={() => fillUser(u.email)}
                  className="text-left p-2.5 rounded-lg border border-gray-200 hover:bg-gray-50 transition-colors">
                  <p className="text-xs font-medium text-gray-800">{u.name}</p>
                </button>
              ))}
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
