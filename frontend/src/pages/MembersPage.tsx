import { useState, useEffect } from 'react';
import type { Loan } from '../types';
import { libraryApi } from '../api/LibraryApiService';
import type { MemberProfile } from '../api/LibraryApiService';
import { useAuth } from '../context/AuthContext';
import { eventBus, Events } from '../patterns/EventBus';

function avatar(name: string) {
  return name.split(' ').map(n => n[0]).join('').toUpperCase().slice(0, 2);
}

export default function MembersPage() {
  const { user } = useAuth();
  const [members, setMembers] = useState<MemberProfile[]>([]);
  const [selected, setSelected] = useState<MemberProfile | null>(null);
  const [loans, setLoans] = useState<Loan[]>([]);
  const [loadingMembers, setLoadingMembers] = useState(true);
  const [loadingLoans, setLoadingLoans] = useState(false);
  const [filter, setFilter] = useState<'all' | 'active' | 'returned'>('all');

  useEffect(() => {
    libraryApi.getAllMembers()
      .then(list => { setMembers(list); })
      .catch(() => eventBus.publish(Events.ERROR, 'Nu s-au putut încărca membrii'))
      .finally(() => setLoadingMembers(false));
  }, []);

  async function selectMember(m: MemberProfile) {
    setSelected(m);
    setFilter('all');
    setLoadingLoans(true);
    try { setLoans(await libraryApi.getLoansForMember(m.memberId)); }
    catch { eventBus.publish(Events.ERROR, 'Eroare la încărcarea împrumuturilor'); }
    finally { setLoadingLoans(false); }
  }

  const filtered = loans.filter(l =>
    filter === 'all' ? true : filter === 'active' ? l.active : !l.active);

  const activeCount = loans.filter(l => l.active).length;

  return (
    <div className="space-y-6">
      <h1 className="text-2xl font-bold text-gray-800">Membri bibliotecă</h1>

      <div className="grid grid-cols-2 sm:grid-cols-3 lg:grid-cols-6 gap-3">
        {loadingMembers
          ? Array.from({ length: 6 }).map((_, i) => (
              <div key={i} className="h-24 bg-gray-200 rounded-2xl animate-pulse" />
            ))
          : members.map(m => {
              const isMe = m.memberId === user?.memberId;
              const isSel = selected?.memberId === m.memberId;
              return (
                <button key={m.memberId} onClick={() => selectMember(m)}
                  className={`p-4 rounded-2xl border text-center transition-all ${
                    isSel ? 'border-blue-500 bg-blue-50 shadow-sm' :
                    'border-gray-200 bg-white hover:border-blue-300 hover:bg-blue-50'
                  }`}>
                  <div className={`w-10 h-10 rounded-full mx-auto flex items-center justify-center text-sm font-bold mb-2 ${
                    isMe ? 'bg-blue-600 text-white' : 'bg-gray-200 text-gray-600'}`}>
                    {avatar(m.displayName)}
                  </div>
                  <p className="text-xs font-semibold text-gray-800 truncate">{m.displayName.split(' ')[0]}</p>
                  <p className="text-xs text-gray-400">{m.memberId}</p>
                  {isMe && <span className="text-xs text-blue-600 font-medium">Tu</span>}
                </button>
              );
            })
        }
      </div>

      {selected && (
        <div className="bg-white rounded-2xl shadow-sm border border-gray-200 p-5 space-y-4">
          <div className="flex items-center justify-between flex-wrap gap-3">
            <div className="flex items-center gap-3">
              <div className="w-10 h-10 rounded-full bg-blue-600 flex items-center justify-center text-white text-sm font-bold">
                {avatar(selected.displayName)}
              </div>
              <div>
                <p className="font-semibold text-gray-800">{selected.displayName}</p>
                <p className="text-xs text-gray-400">{selected.email} · {selected.memberId}</p>
              </div>
            </div>
            <div className="flex items-center gap-3">
              <span className="text-sm text-gray-500">{activeCount} active / {loans.length} total</span>
              <div className="flex gap-1">
                {(['all', 'active', 'returned'] as const).map(f => (
                  <button key={f} onClick={() => setFilter(f)}
                    className={`px-3 py-1 text-xs rounded-lg font-medium transition-colors ${filter === f ? 'bg-blue-600 text-white' : 'bg-gray-100 text-gray-600 hover:bg-gray-200'}`}>
                    {f === 'all' ? 'Toate' : f === 'active' ? 'Active' : 'Returnate'}
                  </button>
                ))}
              </div>
            </div>
          </div>

          {loadingLoans ? (
            <div className="flex justify-center py-8"><div className="animate-spin rounded-full h-8 w-8 border-b-2 border-blue-600" /></div>
          ) : filtered.length === 0 ? (
            <p className="text-center text-gray-400 text-sm py-8">Niciun împrumut.</p>
          ) : (
            <div className="space-y-2">
              {filtered.map(l => {
                const days = Math.ceil((new Date(l.dueDate).getTime() - Date.now()) / 86_400_000);
                const overdue = l.active && days < 0;
                return (
                  <div key={l.loanId} className={`flex items-center justify-between p-3 rounded-xl border text-sm ${overdue ? 'border-red-200 bg-red-50' : l.active ? 'border-blue-200 bg-blue-50' : 'border-gray-200 bg-gray-50'}`}>
                    <div>
                      <span className={`text-xs font-bold mr-2 ${overdue ? 'text-red-600' : l.active ? 'text-blue-600' : 'text-gray-400'}`}>
                        {overdue ? 'ÎNTÂRZIAT' : l.active ? 'ACTIV' : 'RETURNAT'}
                      </span>
                      <span className="font-medium text-gray-800">Item: {l.itemId}</span>
                    </div>
                    <div className="text-right text-xs text-gray-500">
                      <p>Împrumutat: {l.loanDate}</p>
                      <p>Scadent: <span className={overdue ? 'text-red-600 font-medium' : ''}>{l.dueDate}</span></p>
                    </div>
                  </div>
                );
              })}
            </div>
          )}
        </div>
      )}
    </div>
  );
}
