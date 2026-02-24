import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, tap } from 'rxjs';
import { AuthenticationServiceService } from '../../api/services';
import { Register$Params } from '../../api/fn/authentication-service/register';
import { AuthUserResponse, UserSummary } from '../../api/models';
import { Login$Params } from '../../api/fn/authentication-service/login';
import { Router } from '@angular/router';

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  private readonly TOKEN_KEY = 'accessToken';
  private readonly USER_KEY = 'currentUser';


  constructor(private auth: AuthenticationServiceService,private router:Router
  ) {}

 register(request: Register$Params): Observable<AuthUserResponse> {
  return this.auth.register(request).pipe(
    tap(value => {
      localStorage.setItem(this.TOKEN_KEY, value.accessToken as string);
      localStorage.setItem(this.USER_KEY, JSON.stringify(value.user));
    })
  );
}

 login(request: Login$Params): Observable<AuthUserResponse> {
  return this.auth.login(request).pipe(
    tap(value => {
      localStorage.setItem(this.TOKEN_KEY, value.accessToken as string);
      localStorage.setItem(this.USER_KEY, JSON.stringify(value.user));
    })
  );
}


  saveToken(token: string) {
    localStorage.setItem(this.TOKEN_KEY, token);
  }

  logout() {
    localStorage.removeItem(this.TOKEN_KEY);
     localStorage.removeItem(this.USER_KEY);
     this.router.navigate(['login'])
     
  }

  getToken(): string | null {
    return localStorage.getItem(this.TOKEN_KEY);
  }

  getUserId(): string | undefined {

    return this.getUser()?.id;
  }

  getUser(): UserSummary | null {
  const userJson = localStorage.getItem(this.USER_KEY);
  return userJson ? JSON.parse(userJson) : null;
}
}
