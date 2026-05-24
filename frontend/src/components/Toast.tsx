// Observer subscriber: Toast se aboneaza la EventBus si afiseaza notificari.
import React, { createContext, useContext, useEffect, useState, useCallback } from 'react';
import { eventBus, Events } from '../patterns/EventBus';

interface ToastItem {
  id: number;
  message: string;
  type: 'success' | 'error' | 'info';
}

interface ToastContextValue {
  show: (message: string, type?: ToastItem['type']) => void;
}

const ToastContext = createContext<ToastContextValue>({ show: () => {} });

let nextId = 0;

export function ToastProvider({ children }: { children: React.ReactNode }) {
  const [toasts, setToasts] = useState<ToastItem[]>([]);

  const show = useCallback((message: string, type: ToastItem['type'] = 'info') => {
    const id = nextId++;
    setToasts(prev => [...prev, { id, message, type }]);
    setTimeout(() => setToasts(prev => prev.filter(t => t.id !== id)), 3500);
  }, []);

  // Abonat la EventBus (Observer pattern)
  useEffect(() => {
    const unsub1 = eventBus.subscribe<string>(Events.ITEM_BORROWED, msg => show(msg, 'success'));
    const unsub2 = eventBus.subscribe<string>(Events.ITEM_RETURNED, msg => show(msg, 'info'));
    const unsub3 = eventBus.subscribe<string>(Events.CART_UPDATED, msg => show(msg, 'info'));
    const unsub4 = eventBus.subscribe<string>(Events.ERROR, msg => show(msg, 'error'));
    return () => { unsub1(); unsub2(); unsub3(); unsub4(); };
  }, [show]);

  const colors: Record<ToastItem['type'], string> = {
    success: 'bg-green-600',
    error: 'bg-red-600',
    info: 'bg-blue-600',
  };

  return (
    <ToastContext.Provider value={{ show }}>
      {children}
      <div className="fixed bottom-4 right-4 flex flex-col gap-2 z-50">
        {toasts.map(t => (
          <div
            key={t.id}
            className={`${colors[t.type]} text-white px-4 py-3 rounded-lg shadow-lg text-sm max-w-xs animate-fade-in`}
          >
            {t.message}
          </div>
        ))}
      </div>
    </ToastContext.Provider>
  );
}

export const useToast = () => useContext(ToastContext);
