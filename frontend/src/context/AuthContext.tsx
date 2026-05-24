import React, { createContext, useContext, useEffect, useState } from 'react';
import { libraryApi, setAuthToken, getAuthToken } from '../api/LibraryApiService';
import type { MemberProfile } from '../api/LibraryApiService';

interface AuthContextValue {
  user: MemberProfile | null;
  isAuthenticated: boolean;
  isLoading: boolean;
  login: (username: string, password: string) => Promise<void>;
  logout: () => void;
}

const AuthContext = createContext<AuthContextValue>({
  user: null,
  isAuthenticated: false,
  isLoading: true,
  login: async () => {},
  logout: () => {},
});

export function AuthProvider({ children }: { children: React.ReactNode }) {
  const [user, setUser] = useState<MemberProfile | null>(null);
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    const token = getAuthToken();
    if (!token) { setIsLoading(false); return; }
    libraryApi.getMe()
      .then(profile => setUser(profile))
      .catch(() => setAuthToken(null))
      .finally(() => setIsLoading(false));
  }, []);

  async function login(username: string, password: string) {
    const res = await libraryApi.login(username, password);
    setAuthToken(res.token);
    setUser(res.member);
  }

  function logout() {
    setAuthToken(null);
    setUser(null);
  }

  return (
    <AuthContext.Provider value={{ user, isAuthenticated: user !== null, isLoading, login, logout }}>
      {children}
    </AuthContext.Provider>
  );
}

export const useAuth = () => useContext(AuthContext);
