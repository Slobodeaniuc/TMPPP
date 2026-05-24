import { useState, useEffect } from 'react';
import type { Loan } from '../types';
import { libraryApi } from '../api/LibraryApiService';
import { useAuth } from '../context/AuthContext';
import { AddToCartCommand, CheckoutCartCommand } from '../patterns/commands';
import { eventBus, Events } from '../patterns/EventBus';

export default function CartPage() {
  const { user } = useAuth();
  const memberId = user!.memberId;

  const [itemId, setItemId] = useState('');
  const [cartItems, setCartItems] = useState<string[]>([]);
  const [checkedOut, setCheckedOut] = useState<Loan[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    libraryApi.getCart(memberId)
      .then(setCartItems)
      .catch(() => eventBus.publish(Events.ERROR, 'Nu s-a putut încărca coșul'))
      .finally(() => setLoading(false));
  }, [memberId]);

  async function handleAdd() {
    const id = itemId.trim();
    if (!id) return;
    try {
      const items = await new AddToCartCommand(libraryApi, memberId, id).execute();
      setCartItems(items);
      setItemId('');
      eventBus.publish(Events.CART_UPDATED, `${id} adăugat în coș`);
    } catch (e: unknown) {
      const msg = (e as { response?: { data?: { message?: string } } })?.response?.data?.message ?? 'Eroare';
      eventBus.publish(Events.ERROR, msg);
    }
  }

  async function handleRemove(id: string) {
    const res = await libraryApi.removeFromCart(memberId, id);
    setCartItems(res.items);
    eventBus.publish(Events.CART_UPDATED, `${id} eliminat din coș`);
  }

  async function handleUndo() {
    const res = await libraryApi.undoCart(memberId);
    setCartItems(res.items);
    eventBus.publish(Events.CART_UPDATED, res.undone ? 'Undo aplicat' : 'Nimic de anulat');
  }

  async function handleCheckout() {
    try {
      const loans = await new CheckoutCartCommand(libraryApi, memberId).execute();
      setCheckedOut(loans);
      setCartItems([]);
      eventBus.publish(Events.ITEM_BORROWED, `✓ ${loans.length} item(e) împrumutate`);
    } catch (e: unknown) {
      const msg = (e as { response?: { data?: { message?: string } } })?.response?.data?.message ?? 'Checkout eșuat';
      eventBus.publish(Events.ERROR, msg);
    }
  }

  return (
    <div className="max-w-xl mx-auto space-y-6">
      <div className="flex items-center justify-between">
        <h1 className="text-2xl font-bold text-gray-800">Coșul meu</h1>
        <span className="text-sm text-gray-500">{user?.displayName}</span>
      </div>

      <div className="bg-white rounded-2xl shadow-sm border border-gray-200 p-5 space-y-4">
        <div className="flex gap-3">
          <input value={itemId} onChange={e => setItemId(e.target.value)}
            onKeyDown={e => e.key === 'Enter' && handleAdd()}
            placeholder="ID item (ex: B5, D3)"
            className="flex-1 border border-gray-300 rounded-xl px-4 py-2.5 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
          />
          <button onClick={handleAdd}
            className="bg-blue-600 text-white px-5 py-2.5 rounded-xl text-sm font-semibold hover:bg-blue-700 transition-colors">
            Adaugă
          </button>
        </div>

        {loading ? (
          <div className="flex justify-center py-4"><div className="animate-spin rounded-full h-6 w-6 border-b-2 border-blue-600" /></div>
        ) : cartItems.length === 0 ? (
          <div className="text-center py-8">
            <p className="text-gray-400 text-sm">Coșul este gol.</p>
            <p className="text-gray-300 text-xs mt-1">Adaugă items din Catalog sau tastează ID-ul de mai sus.</p>
          </div>
        ) : (
          <ul className="space-y-2">
            {cartItems.map(id => (
              <li key={id} className="flex items-center justify-between bg-gray-50 rounded-xl px-4 py-3 border border-gray-100">
                <div className="flex items-center gap-3">
                  <div className="w-8 h-8 bg-blue-100 rounded-lg flex items-center justify-center text-blue-700 text-xs font-bold">{id}</div>
                  <span className="text-sm font-medium text-gray-700">{id}</span>
                </div>
                <button onClick={() => handleRemove(id)}
                  className="text-xs text-red-500 hover:text-red-700 font-medium">
                  Elimină
                </button>
              </li>
            ))}
          </ul>
        )}

        {!loading && (
          <div className="flex gap-3 pt-2">
            <button onClick={handleUndo}
              className="flex-1 bg-yellow-50 border border-yellow-200 text-yellow-700 px-4 py-2.5 rounded-xl text-sm font-medium hover:bg-yellow-100 transition-colors">
              ↩ Undo
            </button>
            <button onClick={handleCheckout} disabled={cartItems.length === 0}
              className="flex-1 bg-green-600 text-white px-4 py-2.5 rounded-xl text-sm font-semibold hover:bg-green-700 disabled:opacity-40 transition-colors">
              Finalizează ({cartItems.length})
            </button>
          </div>
        )}
      </div>

      {checkedOut.length > 0 && (
        <div className="bg-green-50 rounded-2xl border border-green-200 p-5">
          <h2 className="font-semibold text-green-800 mb-3">✓ Împrumuturi finalizate</h2>
          <div className="space-y-2">
            {checkedOut.map(l => (
              <div key={l.loanId} className="flex justify-between text-sm">
                <span className="font-medium text-green-700">{l.itemId}</span>
                <span className="text-green-600">Scadent: {l.dueDate}</span>
              </div>
            ))}
          </div>
        </div>
      )}
    </div>
  );
}
