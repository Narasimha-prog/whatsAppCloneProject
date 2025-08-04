import { Injectable } from '@angular/core';
import Keycloak from 'keycloak-js';
@Injectable({
  providedIn: 'root'
})
export class KeycloakService {
  private _keycloak: Keycloak | undefined;
  constructor() { }

  get keycloak() {

    if(!this._keycloak){
    this._keycloak=new Keycloak({
      url: "http://localhost:8180",
      realm: "WhatsClone",
      clientId: "WhatsClone"
    });
  }

  return this._keycloak;
}
  async init() {
    const authenticated = await this.keycloak.init({
      onLoad: 'login-required'
    });

    if (authenticated) {
      this.startTokenRefreshTimer(); // 🔄 Start the refresh loop
    }
  }

private startTokenRefreshTimer() {
  setInterval(() => {
    this.updateTokenIfNeeded(30);
  }, 60000); // Every minute
}

async login(){
  await this.keycloak.login();
}

get userId():string{
  return this.keycloak?.tokenParsed?.sub as string;
}


 get isTokenValid() {
  return !this.keycloak.isTokenExpired();
}


 get fullName() : string {
  return this.keycloak.tokenParsed?.['name'] as string;
}
logOut(){
  return this.keycloak.logout({redirectUri: 'http://localhost:4200'});
}
// logIn(){
//   return this.keycloak.login({redirectUri: 'http://localhost:4200'});
// }

accountManagement(){
  return this.keycloak.accountManagement();
}

async updateTokenIfNeeded(minValidity = 30): Promise<boolean> {
    try {
      return await this.keycloak.updateToken(minValidity);
    } catch (err) {
      console.error('Token refresh failed', err);
      this.keycloak.logout(); // or route to login
      return false;
    }
  }

}

