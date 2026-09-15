import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ApiResponse, Beneficiary } from '../models/banking.models';

@Injectable({
  providedIn: 'root'
})
export class BeneficiaryService {
  private apiUrl = 'http://localhost:8080/api/v1/beneficiaries';

  constructor(private http: HttpClient) {}

  addBeneficiary(data: { name: string; accountNumber: string; bankName: string }): Observable<ApiResponse<Beneficiary>> {
    return this.http.post<ApiResponse<Beneficiary>>(this.apiUrl, data);
  }

  getMyBeneficiaries(): Observable<ApiResponse<Beneficiary[]>> {
    return this.http.get<ApiResponse<Beneficiary[]>>(this.apiUrl);
  }

  deleteBeneficiary(id: number): Observable<ApiResponse<string>> {
    return this.http.delete<ApiResponse<string>>(`${this.apiUrl}/${id}`);
  }
}