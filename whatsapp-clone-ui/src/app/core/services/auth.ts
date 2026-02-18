import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { AuthResponse, RegisterRequest } from '../model/auth.model';
import { jwtDecode } from 'jwt-decode';

@Injectable({
  providedIn: 'root'
})

export class AuthService {

   private TOKEN_KEY = 'token';
 private apiUrl = 'http://localhost:8080/api/v1/auth';

  constructor(private http: HttpClient) {}

  register(request: RegisterRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.apiUrl}/register`, request);
  }

  login(request: any): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.apiUrl}/login`, request);
  }

  saveToken(token: string) {
    localStorage.setItem('accessToken', token);
  }

  logout() {
    localStorage.removeItem('accessToken');
  }

  getUserId(): string | undefined {
    const token = this.getToken();
    if (!token) return undefined;

    const decoded: any = jwtDecode(token);
    return decoded.sub; // because backend sets subject = userId
  }
  getToken(): string | null {
    return localStorage.getItem(this.TOKEN_KEY);
  }
}
