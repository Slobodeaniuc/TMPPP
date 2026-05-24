// PATTERN: Strategy
// Algoritmi de sortare interschimbabili la runtime fara sa schimbam codul consumator.
import type { LibraryItem } from '../types';

export interface SortStrategy {
  key: string;
  label: string;
  sort(items: LibraryItem[]): LibraryItem[];
}

export class SortByTitle implements SortStrategy {
  key = 'title';
  label = 'Titlu A→Z';
  sort(items: LibraryItem[]): LibraryItem[] {
    return [...items].sort((a, b) => a.title.localeCompare(b.title));
  }
}

export class SortByType implements SortStrategy {
  key = 'type';
  label = 'Tip';
  sort(items: LibraryItem[]): LibraryItem[] {
    return [...items].sort((a, b) => a.type.localeCompare(b.type));
  }
}

export class SortByAuthor implements SortStrategy {
  key = 'author';
  label = 'Autor';
  sort(items: LibraryItem[]): LibraryItem[] {
    return [...items].sort((a, b) => (a.author ?? '').localeCompare(b.author ?? ''));
  }
}

export class SortById implements SortStrategy {
  key = 'id';
  label = 'ID';
  sort(items: LibraryItem[]): LibraryItem[] {
    return [...items].sort((a, b) => a.id.localeCompare(b.id, undefined, { numeric: true }));
  }
}

export const sortStrategies: SortStrategy[] = [
  new SortByTitle(),
  new SortByType(),
  new SortByAuthor(),
  new SortById(),
];
