// PATTERN: Composite (frontend)
// GroupCard poate contine alte card-uri (inclusiv alte GroupCard-uri),
// oglindind structura LibraryItemGroup din backend.
import type { LibraryItem } from '../types';
import { ItemCardFactory } from '../patterns/ItemCardFactory';

interface Props {
  item: LibraryItem;
  allItems?: LibraryItem[];
  onBorrow?: (id: string) => void;
  onAddToCart?: (id: string) => void;
  depth?: number;
}

export default function GroupCard({ item, allItems = [], onBorrow, onAddToCart, depth = 0 }: Props) {
  const children = allItems.filter(i => item.childIds?.includes(i.id));

  return (
    <div className={`bg-white rounded-xl shadow p-4 border-l-4 border-purple-500 flex flex-col gap-3 ${depth > 0 ? 'border-l-2 shadow-none bg-purple-50' : ''}`}>
      <div className="flex justify-between items-start">
        <span className="text-xs font-bold text-purple-600 uppercase tracking-wide">
          Group {depth > 0 ? `(nesting ${depth})` : ''}
        </span>
        <span className="text-xs text-gray-400">{item.id}</span>
      </div>
      <h3 className="font-semibold text-gray-800">{item.title}</h3>
      {children.length > 0 && (
        <div className="pl-3 border-l-2 border-purple-200 space-y-2">
          {children.map(child => (
            <ItemCardFactory
              key={child.id}
              item={child}
              allItems={allItems}
              onBorrow={onBorrow}
              onAddToCart={onAddToCart}
              depth={depth + 1}
            />
          ))}
        </div>
      )}
      {children.length === 0 && item.childIds && item.childIds.length > 0 && (
        <p className="text-xs text-gray-400">Children: {item.childIds.join(', ')}</p>
      )}
    </div>
  );
}
