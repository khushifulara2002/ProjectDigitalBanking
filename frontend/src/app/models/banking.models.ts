export interface ApiResponse<T> {
  timestamp: string;
  status: number;
  message: string;
  data: T;
}

export interface AuthResponse {
  token: string;
  tokenType: string;
  id: number;
  email: string;
  firstName: string;
  lastName: string;
  roles: string[];
}

export interface UserProfile {
  id: number;
  firstName: string;
  lastName: string;
  email: string;
  phoneNumber: string;
  status: string;
  roles: string[];
  createdAt: string;
}

export interface BankAccount {
  id: number;
  accountNumber: string;
  accountType: 'SAVINGS' | 'CHECKING';
  balance: number;
  status: 'ACTIVE' | 'BLOCKED' | 'CLOSED';
  blockReason?: string;
  ownerName?: string;
  userEmail?: string;
  createdAt: string;
}

export interface Transaction {
  id: number;
  transactionReference: string;
  sourceAccountNumber: string;
  targetAccountNumber: string;
  sourceAccountOwnerName?: string;
  targetAccountOwnerName?: string;
  type: 'DEPOSIT' | 'WITHDRAWAL' | 'TRANSFER';
  amount: number;
  status: 'SUCCESS' | 'FAILED' | 'PENDING';
  description: string;
  createdAt: string;
}

export interface Beneficiary {
  id: number;
  name: string;
  accountNumber: string;
  bankName: string;
  createdAt: string;
}

export interface PagedResponse<T> {
  content: T[];
  pageNumber: number;
  pageSize: number;
  totalElements: number;
  totalPages: number;
  last: boolean;
}

export interface AuditLog {
  id: number;
  action: string;
  performedBy: string;
  details: string;
  timestamp: string;
}