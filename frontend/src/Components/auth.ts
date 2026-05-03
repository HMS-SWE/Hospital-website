import type { RegisterFormData } from "../Pages/Register";

const API_URL = "http://localhost:8080/api";
const ACCESS_TOKEN_KEY = "token";
const REFRESH_TOKEN_KEY = "refreshToken";
const ROLE_KEY = "role";
const EXPIRES_AT_KEY = "expiresAt";

export type UserRole = "PATIENT" | "DOCTOR" | "ADMIN";

export type AuthSession = {
  accessToken: string;
  refreshToken: string;
  role: UserRole;
  expiresAt: number;
};

export type LoginPayload = {
  email: string;
  password: string;
};

export type LoginResponse = {
  accessToken: string;
  refreshToken: string;
  role: UserRole;
  expiresIn: number;
};

export const logout = () => {
    localStorage.removeItem("token");
    window.location.href = "/";
    clearSession();
    };
export type RegisterResponse = {
  userId: number;
  email: string;
  accessToken: string;
  refreshToken: string;
  role: UserRole;
  expiresIn: number;
};

export const saveSession = (
    accessToken: string,
    refreshToken: string,
    role: UserRole,
    expiresInSeconds: number
) => {
  const expiresAt = Date.now() + expiresInSeconds * 1000;

  localStorage.setItem(ACCESS_TOKEN_KEY, accessToken);
  localStorage.setItem(REFRESH_TOKEN_KEY, refreshToken);
  localStorage.setItem(ROLE_KEY, role);
  localStorage.setItem(EXPIRES_AT_KEY, String(expiresAt));
};

export const clearSession = () => {
  localStorage.removeItem(ACCESS_TOKEN_KEY);
  localStorage.removeItem(REFRESH_TOKEN_KEY);
  localStorage.removeItem(ROLE_KEY);
  localStorage.removeItem(EXPIRES_AT_KEY);
  localStorage.removeItem("jwt");
};

export const getSession = (): AuthSession | null => {
  const accessToken = localStorage.getItem(ACCESS_TOKEN_KEY);
  const refreshToken = localStorage.getItem(REFRESH_TOKEN_KEY);
  const role = localStorage.getItem(ROLE_KEY) as UserRole | null;
  const expiresAt = Number(localStorage.getItem(EXPIRES_AT_KEY));

  if (!accessToken || !refreshToken || !role || !Number.isFinite(expiresAt)) {
    return null;
  }

  return {
    accessToken,
    refreshToken,
    role,
    expiresAt,
  };
};

export const isTokenExpired = () => {
  const session = getSession();

  if (!session) {
    return true;
  }

  return Date.now() >= session.expiresAt;
};

export const login = async (payload: LoginPayload): Promise<LoginResponse> => {
  const res = await fetch(`${API_URL}/auth/login`, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify(payload),
  });

  const data = await res.json().catch(() => null);

  if (!res.ok) {
    throw new Error(data?.message || "Login failed");
  }

  saveSession(data.accessToken, data.refreshToken, data.role, data.expiresIn);
  return data;
};

export const register = async (
    formData: RegisterFormData
): Promise<RegisterResponse> => {
  const res = await fetch(`${API_URL}/auth/register`, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify({
      firstName: formData.firstName,
      middleName: formData.middleName,
      lastName: formData.lastName,
      nationalId: formData.nationalId,
      dob: formData.dob,
      gender: formData.gender,
      email: formData.email,
      phone: formData.phone,
      emergency: formData.emergency,
      password: formData.password,
      role: "patient",
    }),
  });

  const data = await res.json().catch(() => null);

  if (!res.ok) {
    throw new Error(data?.message || "Registration failed");
  }

  saveSession(data.accessToken, data.refreshToken, data.role, data.expiresIn);
  return data;
};

export const refreshAccessToken = async (): Promise<string | null> => {
  const session = getSession();

  if (!session?.refreshToken) {
    clearSession();
    return null;
  }

  const res = await fetch(`${API_URL}/auth/refresh`, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify({
      refreshToken: session.refreshToken,
    }),
  });

  const data = await res.json().catch(() => null);

  if (!res.ok) {
    clearSession();
    return null;
  }

  saveSession(data.accessToken, data.refreshToken, data.role, data.expiresIn);
  return data.accessToken;
};

export const getValidToken = async (): Promise<string | null> => {
  const session = getSession();

  if (!session) {
    return null;
  }

  if (Date.now() < session.expiresAt) {
    return session.accessToken;
  }

  return refreshAccessToken();
};

export const authFetch = async (
    input: RequestInfo | URL,
    init: RequestInit = {}
) => {
  let token = await getValidToken();

  const headers = new Headers(init.headers || {});
  if (!headers.has("Content-Type")) {
    headers.set("Content-Type", "application/json");
  }

  if (token) {
    headers.set("Authorization", `Bearer ${token}`);
  }

  let response = await fetch(input, {
    ...init,
    headers,
  });

  if (response.status === 401) {
    token = await refreshAccessToken();

    if (!token) {
      clearSession();
      return response;
    }

    headers.set("Authorization", `Bearer ${token}`);

    response = await fetch(input, {
      ...init,
      headers,
    });
  }

  return response;
};
