// PATTERN: Command
// Actiunile utilizatorului sunt obiecte cu execute().
// Permite logare, retry si eventual undo in afara componentei.
import type { LibraryApiService } from '../../api/LibraryApiService';
import type { Loan, ReturnReceipt } from '../../types';

export interface Command<T> {
  execute(): Promise<T>;
}

export class BorrowCommand implements Command<Loan> {
  constructor(
    private api: LibraryApiService,
    private memberId: string,
    private itemId: string,
  ) {}
  execute(): Promise<Loan> {
    return this.api.borrowItem(this.memberId, this.itemId);
  }
}

export class ReturnCommand implements Command<ReturnReceipt> {
  constructor(
    private api: LibraryApiService,
    private itemId: string,
  ) {}
  execute(): Promise<ReturnReceipt> {
    return this.api.returnItem(this.itemId);
  }
}

export class AddToCartCommand implements Command<string[]> {
  constructor(
    private api: LibraryApiService,
    private memberId: string,
    private itemId: string,
  ) {}
  execute(): Promise<string[]> {
    return this.api.addToCart(this.memberId, this.itemId);
  }
}

export class CheckoutCartCommand implements Command<Loan[]> {
  constructor(
    private api: LibraryApiService,
    private memberId: string,
  ) {}
  execute(): Promise<Loan[]> {
    return this.api.checkoutCart(this.memberId);
  }
}
