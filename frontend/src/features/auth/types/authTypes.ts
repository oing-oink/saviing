export interface LoginResponse {
  accessToken: string;
  customerId: number;
  expiresIn: number;
  name: string;
}

export interface Customer {
  customerId: number;
  name: string;
  pin: string | null;
  oauth2Provider: 'GOOGLE' | null;
  oauth2Id: string | null;
  createdAt: string;
  updatedAt: string;
}

export interface AuthState {
  isAuthenticated: boolean;
  accessToken: string | null;
  customerId: number | null;
  expiresIn: number | null;
}
