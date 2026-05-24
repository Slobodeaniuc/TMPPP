// PATTERN: Observer
// Sistem de publish/subscribe decuplat pentru notificari UI.
// Componentele se abonaeza la evenimente fara sa se cunoasca intre ele.

type Handler<T = unknown> = (data: T) => void;

class EventBus {
  private handlers = new Map<string, Handler[]>();

  subscribe<T>(event: string, handler: Handler<T>): () => void {
    const list = this.handlers.get(event) ?? [];
    list.push(handler as Handler);
    this.handlers.set(event, list);
    return () => {
      const updated = (this.handlers.get(event) ?? []).filter(h => h !== handler);
      this.handlers.set(event, updated);
    };
  }

  publish<T>(event: string, data: T): void {
    (this.handlers.get(event) ?? []).forEach(h => h(data as unknown));
  }
}

export const eventBus = new EventBus();

// Tipuri de evenimente publicate de pagini
export const Events = {
  ITEM_BORROWED: 'ITEM_BORROWED',
  ITEM_RETURNED: 'ITEM_RETURNED',
  CART_UPDATED: 'CART_UPDATED',
  ERROR: 'ERROR',
} as const;
