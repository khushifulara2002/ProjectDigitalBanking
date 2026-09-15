import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ApiResponse, BankAccount } from '../models/banking.models';

@Injectable({
  providedIn: 'root'
})
export class AccountService {
  private apiUrl = 'http://localhost:8080/api/v1/accounts';

  constructor(private http: HttpClient) {}

  createAccount(accountData: { accountType: string; initialDeposit: number }): Observable<ApiResponse<BankAccount>> {
    return this.http.post<ApiResponse<BankAccount>>(this.apiUrl, accountData);
  }

  getMyAccounts(): Observable<ApiResponse<BankAccount[]>> {
    return this.http.get<ApiResponse<BankAccount[]>>(`${this.apiUrl}/me`);
  }

  getAccountByNumber(accountNumber: string): Observable<ApiResponse<BankAccount>> {
    return this.http.get<ApiResponse<BankAccount>>(`${this.apiUrl}/${accountNumber}`);
  }

  deleteAccount(accountNumber: string): Observable<ApiResponse<string>> {
    return this.http.delete<ApiResponse<string>>(`${this.apiUrl}/${accountNumber}`);
  }
}