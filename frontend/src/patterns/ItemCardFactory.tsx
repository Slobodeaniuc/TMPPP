// PATTERN: Factory Method
// Fiecare Creator stie cum sa randeze tipul sau de item.
// Consumatorul primeste componenta corecta fara if/else.
import type { LibraryItem } from '../types';
import BookCard from '../components/BookCard';
import MagazineCard from '../components/MagazineCard';
import DvdCard from '../components/DvdCard';
import GroupCard from '../components/GroupCard';

export interface ItemCardProps {
  item: LibraryItem;
  allItems?: LibraryItem[];
  onBorrow?: (id: string) => void;
  onAddToCart?: (id: string) => void;
  depth?: number;
}

interface ItemCardCreator {
  create(props: ItemCardProps): React.ReactElement;
}

class BookCardCreator implements ItemCardCreator {
  create(props: ItemCardProps) { return <BookCard {...props} />; }
}

class MagazineCardCreator implements ItemCardCreator {
  create(props: ItemCardProps) { return <MagazineCard {...props} />; }
}

class DvdCardCreator implements ItemCardCreator {
  create(props: ItemCardProps) { return <DvdCard {...props} />; }
}

class GroupCardCreator implements ItemCardCreator {
  create(props: ItemCardProps) { return <GroupCard {...props} />; }
}

const creators: Record<string, ItemCardCreator> = {
  BOOK: new BookCardCreator(),
  MAGAZINE: new MagazineCardCreator(),
  DVD: new DvdCardCreator(),
  GROUP: new GroupCardCreator(),
};

export function ItemCardFactory(props: ItemCardProps): React.ReactElement {
  const creator = creators[props.item.type];
  if (!creator) return <div className="text-red-500 text-xs">Unknown: {props.item.type}</div>;
  return creator.create(props);
}
