export interface LibraryItem {
  id: string;
  title: string;
  type: 'BOOK' | 'MAGAZINE' | 'DVD' | 'GROUP';
  author?: string;
  isbn?: string;
  issueNumber?: number;
  durationMinutes?: number;
  childIds?: string[];
}

export interface Loan {
  loanId: string;
  memberId: string;
  itemId: string;
  loanDate: string;
  dueDate: string;
  returnDate?: string;
  active: boolean;
}

export interface ReturnReceipt {
  loan: Loan;
  penalty: string;
  evaluatedOn: string;
}

export interface PenaltyDto {
  loanId: string;
  penalty: string;
}

export interface ApiError {
  error: string;
  message: string;
}
