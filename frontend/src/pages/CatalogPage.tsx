import { useEffect, useState, useMemo } from 'react';
import type { LibraryItem } from '../types';
import { libraryApi } from '../api/LibraryApiService';
import { useAuth } from '../context/AuthContext';
import { ItemCardFactory } from '../patterns/ItemCardFactory';
import { sortStrategies } from '../patterns/SortStrategy';
import withLoading from '../patterns/withLoading';
import { BorrowCommand, AddToCartCommand } from '../patterns/commands';
import { eventBus, Events } from '../patterns/EventBus';

const TYPES = ['ALL', 'BOOK', 'MAGAZINE', 'DVD', 'GROUP'] as const;
type FilterType = typeof TYPES[number];

const TYPE_LABELS: Record<FilterType, string> = {
  ALL: 'Toate', BOOK: 'Cărți', MAGAZINE: 'Reviste', DVD: 'DVD', GROUP: 'Grupuri',
};

interface GridProps {
  items: LibraryItem[];
  allItems: LibraryItem[];
  onBorrow: (id: string) => void;
  onAddToCart: (id: string) => void;
}

function ItemGrid({ items, allItems, onBorrow, onAddToCart }: GridProps) {
  return (
    <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-4">
      {items.map(item => (
        <ItemCardFactory key={item.id} item={item} allItems={allItems}
          onBorrow={onBorrow} onAddToCart={onAddToCart} />
      ))}
      {items.length === 0 && (
        <p className="col-span-full text-center text-gray-400 py-12 text-sm">Niciun item găsit.</p>
      )}
    </div>
  );
}

const ItemGridWithLoading = withLoading(ItemGrid);

export default function CatalogPage() {
  const { user } = useAuth();
  const memberId = user!.memberId;

  const [items, setItems] = useState<LibraryItem[]>([]);
  const [loading, setLoading] = useState(true);
  const [filter, setFilter] = useState<FilterType>('ALL');
  const [sortKey, setSortKey] = useState('title');
  const [search, setSearch] = useState('');

  useEffect(() => {
    libraryApi.getCatalog()
      .then(setItems)
      .catch(() => eventBus.publish(Events.ERROR, 'Nu s-a putut încărca catalogul'))
      .finally(() => setLoading(false));
  }, []);

  const displayed = useMemo(() => {
    const strategy = sortStrategies.find(s => s.key === sortKey) ?? sortStrategies[0];
    let result = filter === 'ALL' ? items : items.filter(i => i.type === filter);
    if (search.trim()) {
      const q = search.toLowerCase();
      result = result.filter(i =>
        i.title.toLowerCase().includes(q) ||
        (i.author ?? '').toLowerCase().includes(q) ||
        i.id.toLowerCase().includes(q)
      );
    }
    return strategy.sort(result);
  }, [items, filter, sortKey, search]);

  const counts = useMemo(() => ({
    ALL: items.length,
    BOOK: items.filter(i => i.type === 'BOOK').length,
    MAGAZINE: items.filter(i => i.type === 'MAGAZINE').length,
    DVD: items.filter(i => i.type === 'DVD').length,
    GROUP: items.filter(i => i.type === 'GROUP').length,
  }), [items]);

  async function handleBorrow(itemId: string) {
    try {
      const loan = await new BorrowCommand(libraryApi, memberId, itemId).execute();
      eventBus.publish(Events.ITEM_BORROWED, `✓ Împrumutat ${loan.itemId} — scadent ${loan.dueDate}`);
    } catch (e: unknown) {
      const msg = (e as { response?: { data?: { message?: string } } })?.response?.data?.message ?? 'Împrumut eșuat';
      eventBus.publish(Events.ERROR, msg);
    }
  }

  async function handleAddToCart(itemId: string) {
    try {
      await new AddToCartCommand(libraryApi, memberId, itemId).execute();
      eventBus.publish(Events.CART_UPDATED, `${itemId} adăugat în coș`);
    } catch (e: unknown) {
      const msg = (e as { response?: { data?: { message?: string } } })?.response?.data?.message ?? 'Eroare coș';
      eventBus.publish(Events.ERROR, msg);
    }
  }

  return (
    <div className="space-y-5">
      <div className="flex flex-wrap items-center justify-between gap-4">
        <h1 className="text-2xl font-bold text-gray-800">Catalog</h1>
        <div className="flex flex-wrap gap-2 items-center">
          <input
            type="text"
            value={search}
            onChange={e => setSearch(e.target.value)}
            placeholder="Caută titlu, autor, ID…"
            className="border border-gray-300 rounded-xl px-3 py-2 text-sm w-52 focus:ring-2 focus:ring-blue-500 focus:outline-none"
          />
          <select
            value={sortKey}
            onChange={e => setSortKey(e.target.value)}
            className="border border-gray-300 rounded-xl px-3 py-2 text-sm focus:ring-2 focus:ring-blue-500 focus:outline-none"
          >
            {sortStrategies.map(s => <option key={s.key} value={s.key}>{s.label}</option>)}
          </select>
        </div>
      </div>

      <div className="flex gap-2 flex-wrap">
        {TYPES.map(t => (
          <button key={t} onClick={() => setFilter(t)}
            className={`px-4 py-2 rounded-xl text-sm font-medium transition-colors ${
              filter === t ? 'bg-blue-600 text-white shadow-sm' : 'bg-white text-gray-600 border border-gray-200 hover:bg-gray-50'
            }`}>
            {TYPE_LABELS[t]}
            <span className={`ml-2 text-xs px-1.5 py-0.5 rounded-full ${filter === t ? 'bg-blue-500' : 'bg-gray-100 text-gray-500'}`}>
              {counts[t]}
            </span>
          </button>
        ))}
      </div>

      <ItemGridWithLoading
        isLoading={loading}
        items={displayed}
        allItems={items}
        onBorrow={handleBorrow}
        onAddToCart={handleAddToCart}
      />
    </div>
  );
}
