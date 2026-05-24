import type { LibraryItem } from '../types';

interface Props {
  item: LibraryItem;
  onBorrow?: (id: string) => void;
  onAddToCart?: (id: string) => void;
}

export default function DvdCard({ item, onBorrow, onAddToCart }: Props) {
  return (
    <div className="bg-white rounded-xl shadow p-4 border-l-4 border-orange-500 flex flex-col gap-2">
      <div className="flex justify-between items-start">
        <span className="text-xs font-bold text-orange-600 uppercase tracking-wide">DVD</span>
        <span className="text-xs text-gray-400">{item.id}</span>
      </div>
      <h3 className="font-semibold text-gray-800 leading-tight">{item.title}</h3>
      <p className="text-sm text-gray-500">{item.durationMinutes} min</p>
      <div className="flex gap-2 mt-auto pt-2">
        {onBorrow && (
          <button
            onClick={() => onBorrow(item.id)}
            className="flex-1 py-1.5 text-xs font-medium bg-orange-600 text-white rounded-lg hover:bg-orange-700 transition-colors"
          >
            Borrow
          </button>
        )}
        {onAddToCart && (
          <button
            onClick={() => onAddToCart(item.id)}
            className="flex-1 py-1.5 text-xs font-medium bg-gray-100 text-gray-700 rounded-lg hover:bg-gray-200 transition-colors"
          >
            + Cart
          </button>
        )}
      </div>
    </div>
  );
}
