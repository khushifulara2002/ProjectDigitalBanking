import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ApiResponse, PagedResponse, Transaction } from '../models/banking.models';

@Injectable({
  providedIn: 'root'
})
export class TransactionService {
  private apiUrl = 'http://localhost:8080/api/v1/transactions';

  constructor(private http: HttpClient) {}

  deposit(depositData: { accountNumber: string; amount: number; description?: string }): Observable<ApiResponse<Transaction>> {
    return this.http.post<ApiResponse<Transaction>>(`${this.apiUrl}/deposit`, depositData);
  }

  withdraw(withdrawData: { accountNumber: string; amount: number; description?: string }): Observable<ApiResponse<Transaction>> {
    return this.http.post<ApiResponse<Transaction>>(`${this.apiUrl}/withdraw`, withdrawData);
  }

  transfer(transferData: { sourceAccountNumber: string; targetAccountNumber: string; amount: number; description?: string }): Observable<ApiResponse<Transaction>> {
    return this.http.post<ApiResponse<Transaction>>(`${this.apiUrl}/transfer`, transferData);
  }

  getAccountTransactions(accountNumber: string, page = 0, size = 10): Observable<ApiResponse<PagedResponse<Transaction>>> {
    return this.http.get<ApiResponse<PagedResponse<Transaction>>>(`${this.apiUrl}/account/${accountNumber}?page=${page}&size=${size}`);
  }

  getStatement(accountNumber: string, type?: string, startDate?: string, endDate?: string, page = 0, size = 10): Observable<ApiResponse<PagedResponse<Transaction>>> {
    let params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());

    if (type) params = params.set('type', type);
    if (startDate) params = params.set('startDate', startDate);
    if (endDate) params = params.set('endDate', endDate);

    return this.http.get<ApiResponse<PagedResponse<Transaction>>>(`${this.apiUrl}/statement/${accountNumber}`, { params });
  }
}