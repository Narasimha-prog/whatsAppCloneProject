export interface RegisterRequest {

  firstName: string;
  lastName: string;
  email: string;
  phoneNumber:string;
  password: string;
}

export interface AuthResponse {
  accessToken: string;
  tokenType: string;
  expiresIn: number;
  issuedAt: number;
  token:string
}