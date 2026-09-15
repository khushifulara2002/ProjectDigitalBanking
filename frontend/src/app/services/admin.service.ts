import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ApiResponse, BankAccount, UserProfile, AuditLog, PagedResponse } from '../models/banking.models';

@Injectable({
  providedIn: 'root'
})
export class AdminService {
  private apiUrl = 'http://localhost:8080/api/v1/admin';

  constructor(private http: HttpClient) {}

  getAllUsers(): Observable<ApiResponse<UserProfile[]>> {
    return this.http.get<ApiResponse<UserProfile[]>>(`${this.apiUrl}/users`);
  }

  getAllAccounts(): Observable<ApiResponse<BankAccount[]>> {
    return this.http.get<ApiResponse<BankAccount[]>>(`${this.apiUrl}/accounts`);
  }

  blockAccount(accountNumber: string): Observable<ApiResponse<BankAccount>> {
    return this.http.put<ApiResponse<BankAccount>>(`${this.apiUrl}/accounts/${accountNumber}/block`, {});
  }

  unblockAccount(accountNumber: string): Observable<ApiResponse<BankAccount>> {
    return this.http.put<ApiResponse<BankAccount>>(`${this.apiUrl}/accounts/${accountNumber}/unblock`, {});
  }

  getAuditLogs(page = 0, size = 10): Observable<ApiResponse<PagedResponse<AuditLog>>> {
    return this.http.get<ApiResponse<PagedResponse<AuditLog>>>(`${this.apiUrl}/audit-logs?page=${page}&size=${size}`);
  }
}
