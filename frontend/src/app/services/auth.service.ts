import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable, tap } from 'rxjs';
import { ApiResponse, AuthResponse, UserProfile } from '../models/banking.models';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private apiUrl = 'http://localhost:8080/api/v1/auth';
  private currentUserSubject = new BehaviorSubject<AuthResponse | null>(null);
  public currentUser$ = this.currentUserSubject.asObservable();

  constructor(private http: HttpClient) {
    const savedUser = localStorage.getItem('auth_user');
    if (savedUser) {
      this.currentUserSubject.next(JSON.parse(savedUser));
    }
  }

  register(userData: any): Observable<ApiResponse<UserProfile>> {
    return this.http.post<ApiResponse<UserProfile>>(`${this.apiUrl}/register`, userData);
  }

  login(credentials: any): Observable<ApiResponse<AuthResponse>> {
    return this.http.post<ApiResponse<AuthResponse>>(`${this.apiUrl}/login`, credentials).pipe(
      tap(response => {
        if (response.data && response.data.token) {
          localStorage.setItem('jwt_token', response.data.token);
          localStorage.setItem('auth_user', JSON.stringify(response.data));
          this.currentUserSubject.next(response.data);
        }
      })
    );
  }

  updateProfile(profileData: any): Observable<ApiResponse<UserProfile>> {
    return this.http.put<ApiResponse<UserProfile>>(`${this.apiUrl}/profile`, profileData).pipe(
      tap(response => {
        if (response.data) {
          const current = this.currentUserSubject.value;
          if (current) {
            const updated = {
              ...current,
              firstName: response.data.firstName,
              lastName: response.data.lastName,
              email: response.data.email
            };
            localStorage.setItem('auth_user', JSON.stringify(updated));
            this.currentUserSubject.next(updated);
          }
        }
      })
    );
  }

  logout(): void {
    localStorage.removeItem('jwt_token');
    localStorage.removeItem('auth_user');
    this.currentUserSubject.next(null);
  }

  getToken(): string | null {
    return localStorage.getItem('jwt_token');
  }

  isLoggedIn(): boolean {
    return !!this.getToken();
  }

  isAdmin(): boolean {
    const user = this.currentUserSubject.value;
    return user ? user.roles.includes('ROLE_ADMIN') : false;
  }
}