const API_BASE_URL = "http://localhost:8080/api";

// JWT decode utility
export function decodeJWT(token: string): { userId: number; role: string; exp: number } | null {
  try {
    const payload = token.split('.')[1];
    const decoded = JSON.parse(atob(payload));
    return {
      userId: parseInt(decoded.sub),
      role: decoded.role,
      exp: decoded.exp
    };
  } catch {
    return null;
  }
}

// API call utility with auth header
export async function apiCall(endpoint: string, options: RequestInit = {}): Promise<Response> {
  const token = localStorage.getItem('token');
  const headers: Record<string, string> = {
    'Content-Type': 'application/json',
    ...(options.headers as Record<string, string>),
  };

  if (token) {
    headers.Authorization = `Bearer ${token}`;
  }

  return fetch(`${API_BASE_URL}${endpoint}`, {
    ...options,
    headers,
  });
}

// Check if token is expired
export function isTokenExpired(token: string): boolean {
  const decoded = decodeJWT(token);
  if (!decoded) return true;
  return Date.now() >= decoded.exp * 1000;
}

// Get current user from token
export function getCurrentUser(): { userId: number; role: string } | null {
  const token = localStorage.getItem('token');
  if (!token || isTokenExpired(token)) {
    return null;
  }
  return decodeJWT(token);
}