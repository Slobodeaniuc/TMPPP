import { useState, useEffect } from 'react';
import type { Loan } from '../types';
import { libraryApi } from '../api/LibraryApiService';
import { useAuth } from '../context/AuthContext';
import { BorrowCommand, ReturnCommand } from '../patterns/commands';
import { eventBus, Events } from '../patterns/EventBus';

function daysLeft(dueDate: string): number {
  return Math.ceil((new Date(dueDate).getTime() - Date.now()) / 86_400_000);
}

function LoanCard({ loan, onReturn }: { loan: Loan; onReturn: (id: string) => void }) {
  const days = daysLeft(loan.dueDate);
  const overdue = loan.active && days < 0;
  const dueSoon = loan.active && days >= 0 && days <= 3;

  return (
    <div className={`p-4 rounded-xl border flex items-center justify-between gap-4 ${
      overdue ? 'border-red-200 bg-red-50' : dueSoon ? 'border-yellow-200 bg-yellow-50' : loan.active ? 'border-blue-200 bg-blue-50' : 'border-gray-200 bg-gray-50'
    }`}>
      <div className="min-w-0">
        <div className="flex items-center gap-2 mb-1">
          <span className={`text-xs font-bold px-2 py-0.5 rounded-full ${
            overdue ? 'bg-red-200 text-red-700' :
            dueSoon ? 'bg-yellow-200 text-yellow-700' :
            loan.active ? 'bg-blue-200 text-blue-700' : 'bg-gray-200 text-gray-600'
          }`}>
            {overdue ? 'ÎNTÂRZIAT' : dueSoon ? 'URGENT' : loan.active ? 'ACTIV' : 'RETURNAT'}
          </span>
          <span className="font-mono text-xs text-gray-400">{loan.loanId.slice(0, 8)}…</span>
        </div>
        <p className="font-semibold text-gray-800">Item: <span className="text-blue-700">{loan.itemId}</span></p>
        <p className="text-xs text-gray-500 mt-0.5">
          Împrumutat: {loan.loanDate} · Scadent: <span className={overdue ? 'text-red-600 font-medium' : ''}>{loan.dueDate}</span>
          {loan.active && <span className={`ml-2 font-medium ${overdue ? 'text-red-600' : dueSoon ? 'text-yellow-600' : 'text-gray-600'}`}>
            ({overdue ? `${-days} zile întârziere` : `${days} zile rămase`})
          </span>}
        </p>
        {loan.returnDate && <p className="text-xs text-gray-400 mt-0.5">Returnat: {loan.returnDate}</p>}
      </div>
      {loan.active && (
        <button onClick={() => onReturn(loan.itemId)}
          className="shrink-0 bg-white border border-red-300 text-red-600 px-4 py-2 rounded-xl text-sm font-medium hover:bg-red-50 transition-colors">
          Returnează
        </button>
      )}
    </div>
  );
}

export default function LoansPage() {
  const { user } = useAuth();
  const memberId = user!.memberId;

  const [itemId, setItemId] = useState('');
  const [loans, setLoans] = useState<Loan[]>([]);
  const [loading, setLoading] = useState(true);
  const [filterActive, setFilterActive] = useState<'all' | 'active' | 'returned'>('all');

  async function fetchLoans() {
    setLoading(true);
    try { setLoans(await libraryApi.getLoansForMember(memberId)); }
    catch { eventBus.publish(Events.ERROR, 'Nu s-au putut încărca împrumuturile'); }
    finally { setLoading(false); }
  }

  useEffect(() => { fetchLoans(); }, [memberId]);

  async function handleBorrow() {
    const id = itemId.trim();
    if (!id) return;
    try {
      const loan = await new BorrowCommand(libraryApi, memberId, id).execute();
      eventBus.publish(Events.ITEM_BORROWED, `✓ Împrumutat ${loan.itemId} — scadent ${loan.dueDate}`);
      setItemId('');
      await fetchLoans();
    } catch (e: unknown) {
      const msg = (e as { response?: { data?: { message?: string } } })?.response?.data?.message ?? 'Eroare';
      eventBus.publish(Events.ERROR, msg);
    }
  }

  async function handleReturn(loanItemId: string) {
    try {
      const receipt = await new ReturnCommand(libraryApi, loanItemId).execute();
      const pen = parseFloat(receipt.penalty);
      eventBus.publish(Events.ITEM_RETURNED,
        pen > 0 ? `✓ Returnat ${loanItemId} — penalitate: ${pen.toFixed(2)} lei` : `✓ Returnat ${loanItemId}`);
      await fetchLoans();
    } catch (e: unknown) {
      const msg = (e as { response?: { data?: { message?: string } } })?.response?.data?.message ?? 'Eroare';
      eventBus.publish(Events.ERROR, msg);
    }
  }

  const filtered = loans.filter(l =>
    filterActive === 'all' ? true : filterActive === 'active' ? l.active : !l.active);
  const activeCount = loans.filter(l => l.active).length;

  return (
    <div className="max-w-3xl mx-auto space-y-6">
      <div className="flex items-center justify-between">
        <h1 className="text-2xl font-bold text-gray-800">Împrumuturile mele</h1>
        <span className="text-sm text-gray-500">{user?.displayName}</span>
      </div>

      {/* Borrow form */}
      <div className="bg-white rounded-2xl shadow-sm border border-gray-200 p-5">
        <h2 className="font-semibold text-gray-700 mb-4">Împrumută un item nou</h2>
        <div className="flex gap-3">
          <input value={itemId} onChange={e => setItemId(e.target.value)}
            onKeyDown={e => e.key === 'Enter' && handleBorrow()}
            placeholder="ID item (ex: B3, M7, D11)"
            className="flex-1 border border-gray-300 rounded-xl px-4 py-2.5 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
          />
          <button onClick={handleBorrow}
            className="bg-blue-600 text-white px-5 py-2.5 rounded-xl text-sm font-semibold hover:bg-blue-700 transition-colors">
            Împrumută
          </button>
        </div>
      </div>

      {/* Loans list */}
      <div className="bg-white rounded-2xl shadow-sm border border-gray-200 p-5 space-y-4">
        <div className="flex items-center justify-between flex-wrap gap-3">
          <div className="flex items-center gap-3">
            <h2 className="font-semibold text-gray-700">Istoricul împrumuturilor</h2>
            <span className="text-xs bg-blue-100 text-blue-700 px-2 py-0.5 rounded-full font-medium">{activeCount} active</span>
          </div>
          <div className="flex gap-1">
            {(['all', 'active', 'returned'] as const).map(f => (
              <button key={f} onClick={() => setFilterActive(f)}
                className={`px-3 py-1 text-xs rounded-lg font-medium transition-colors ${filterActive === f ? 'bg-blue-600 text-white' : 'bg-gray-100 text-gray-600 hover:bg-gray-200'}`}>
                {f === 'all' ? 'Toate' : f === 'active' ? 'Active' : 'Returnate'}
              </button>
            ))}
          </div>
        </div>

        {loading ? (
          <div className="flex justify-center py-8"><div className="animate-spin rounded-full h-8 w-8 border-b-2 border-blue-600" /></div>
        ) : filtered.length === 0 ? (
          <p className="text-center text-gray-400 text-sm py-8">Niciun împrumut găsit.</p>
        ) : (
          <div className="space-y-2">{filtered.map(l => <LoanCard key={l.loanId} loan={l} onReturn={handleReturn} />)}</div>
        )}
      </div>
    </div>
  );
}
