import { NavLink, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

const links = [
  { to: '/',        label: 'Catalog' },
  { to: '/loans',   label: 'Împrumuturi' },
  { to: '/cart',    label: 'Coș' },
  { to: '/members', label: 'Membri' },
];

export default function Navbar() {
  const { user, logout } = useAuth();
  const navigate = useNavigate();

  function handleLogout() {
    logout();
    navigate('/login', { replace: true });
  }

  const initials = user?.displayName
    .split(' ')
    .map(n => n[0])
    .join('')
    .toUpperCase()
    .slice(0, 2) ?? '??';

  return (
    <nav className="bg-gray-900 text-white shadow-lg">
      <div className="container mx-auto px-4 flex items-center justify-between h-14">
        <div className="flex items-center gap-6">
          <span className="font-bold text-base tracking-tight flex items-center gap-2">
            <span>📚</span> LibraryCS
          </span>
          <div className="flex gap-1">
            {links.map(({ to, label }) => (
              <NavLink
                key={to}
                to={to}
                end={to === '/'}
                className={({ isActive }) =>
                  `px-3 py-1.5 rounded-lg text-sm font-medium transition-colors ${
                    isActive ? 'bg-blue-600 text-white' : 'text-gray-300 hover:bg-gray-700'
                  }`
                }
              >
                {label}
              </NavLink>
            ))}
          </div>
        </div>

        <div className="flex items-center gap-3">
          <div className="flex items-center gap-2">
            <div className="w-8 h-8 rounded-full bg-blue-600 flex items-center justify-center text-xs font-bold">
              {initials}
            </div>
            <div className="hidden sm:block">
              <p className="text-sm font-medium text-white leading-none">{user?.displayName}</p>
              <p className="text-xs text-gray-400 leading-none mt-0.5">{user?.memberId}</p>
            </div>
          </div>
          <button
            onClick={handleLogout}
            className="ml-2 px-3 py-1.5 text-xs font-medium text-gray-300 border border-gray-600 rounded-lg hover:bg-gray-700 transition-colors"
          >
            Ieșire
          </button>
        </div>
      </div>
    </nav>
  );
}
