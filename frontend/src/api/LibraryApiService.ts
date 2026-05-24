// PATTERN: Facade — ascunde HTTP + auth complet in spatele acestei interfete
import axios from 'axios';
import type { LibraryItem, Loan, ReturnReceipt, PenaltyDto } from '../types';

export interface MemberProfile {
  memberId: string;
  displayName: string;
  email: string;
}

export interface LoginResponse {
  token: string;
  member: MemberProfile;
}

// ----- Token store -----
let _token: string | null = localStorage.getItem('auth_token');

export function setAuthToken(token: string | null) {
  _token = token;
  if (token) localStorage.setItem('auth_token', token);
  else localStorage.removeItem('auth_token');
}

export function getAuthToken() { return _token; }

// ----- Axios instance -----
const http = axios.create({ baseURL: '/api' });

http.interceptors.request.use(config => {
  if (_token) config.headers.Authorization = `Bearer ${_token}`;
  return config;
});

http.interceptors.response.use(
  res => res,
  err => {
    if (err.response?.status === 401) {
      setAuthToken(null);
      window.location.href = '/login';
    }
    return Promise.reject(err);
  }
);

// ----- Service class -----
export class LibraryApiService {
  // Auth
  login(username: string, password: string): Promise<LoginResponse> {
    return http.post<LoginResponse>('/auth/login', { username, password }).then(r => r.data);
  }
  getMe(): Promise<MemberProfile> {
    return http.get<MemberProfile>('/auth/me').then(r => r.data);
  }
  getAllMembers(): Promise<MemberProfile[]> {
    return http.get<MemberProfile[]>('/auth/members').then(r => r.data);
  }

  // Catalog
  getCatalog(): Promise<LibraryItem[]> {
    return http.get<LibraryItem[]>('/catalog').then(r => r.data);
  }
  getItem(id: string): Promise<LibraryItem> {
    return http.get<LibraryItem>(`/catalog/${id}`).then(r => r.data);
  }
  addBook(data: { id: string; title: string; author: string; isbn: string }): Promise<LibraryItem> {
    return http.post<LibraryItem>('/catalog/books', data).then(r => r.data);
  }
  addMagazine(data: { id: string; title: string; issueNumber: number }): Promise<LibraryItem> {
    return http.post<LibraryItem>('/catalog/magazines', data).then(r => r.data);
  }
  addDvd(data: { id: string; title: string; durationMinutes: number }): Promise<LibraryItem> {
    return http.post<LibraryItem>('/catalog/dvds', data).then(r => r.data);
  }

  // Loans
  borrowItem(memberId: string, itemId: string): Promise<Loan> {
    return http.post<Loan>('/loans/borrow', { memberId, itemId }).then(r => r.data);
  }
  returnItem(itemId: string): Promise<ReturnReceipt> {
    return http.post<ReturnReceipt>(`/loans/${itemId}/return`).then(r => r.data);
  }
  getLoansForMember(memberId: string): Promise<Loan[]> {
    return http.get<Loan[]>(`/loans/member/${memberId}`).then(r => r.data);
  }
  getPenalty(loanId: string, date: string): Promise<PenaltyDto> {
    return http.get<PenaltyDto>(`/loans/${loanId}/penalty`, { params: { date } }).then(r => r.data);
  }

  // Cart
  getCart(memberId: string): Promise<string[]> {
    return http.get<{ items: string[] }>(`/cart/${memberId}`).then(r => r.data.items);
  }
  addToCart(memberId: string, itemId: string): Promise<string[]> {
    return http.post<{ items: string[] }>(`/cart/${memberId}/add`, { itemId }).then(r => r.data.items);
  }
  removeFromCart(memberId: string, itemId: string): Promise<{ removed: boolean; items: string[] }> {
    return http.delete<{ removed: boolean; items: string[] }>(`/cart/${memberId}/remove/${itemId}`).then(r => r.data);
  }
  undoCart(memberId: string): Promise<{ undone: boolean; items: string[] }> {
    return http.post<{ undone: boolean; items: string[] }>(`/cart/${memberId}/undo`).then(r => r.data);
  }
  checkoutCart(memberId: string): Promise<Loan[]> {
    return http.post<Loan[]>(`/cart/${memberId}/checkout`).then(r => r.data);
  }
  clearCart(memberId: string): Promise<void> {
    return http.delete(`/cart/${memberId}/clear`).then(() => undefined);
  }
}

export const libraryApi = new LibraryApiService();
