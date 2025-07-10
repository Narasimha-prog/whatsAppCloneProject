import { HttpHeaders, HttpInterceptorFn } from '@angular/common/http';
import { KeycloakService } from '../keycloak/keycloak';
import { inject } from '@angular/core';

export const keycloakHttpInterceptor: HttpInterceptorFn = (req, next) => {
  const keycloakService=inject(KeycloakService);

  const token=keycloakService.keycloak.token;

  if(token){
    const authReq=req.clone({
      headers: new HttpHeaders({

        Authorization: `Bearer ${token}`
      })
    });
    console.log("Sending request with headers:", authReq.headers);
    return next(authReq);
  }
  return next(req);
};
