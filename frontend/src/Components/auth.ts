import type { RegisterFormData } from "../Pages/Register";
import { apiCall, getCurrentUser } from '../utils/api';

export type User = {
  email: string
  password: string
  role: 'patient' | 'doctor' | 'admin'
}

export type LoginCredentials = {
  emailOrUsername: string
  password: string
}

export const mockUsers: User[] = [
  { email: 'patient@test.com', password: '123456', role: 'patient' },
  { email: 'doctor@test.com', password: '123456', role: 'doctor' },
  { email: 'admin@test.com', password: '123456', role: 'admin' },
]

export function authenticate(credentials: LoginCredentials): User | null {
  const loginId = credentials.emailOrUsername.trim().toLowerCase()
  return (
    mockUsers.find(
      user =>
        user.email === loginId &&
        user.password === credentials.password,
    ) || null
  )
}

export const logout = () => {
    localStorage.removeItem("token");
    localStorage.removeItem("user");
    window.location.href = "/";
    };


export const register = async (formData: RegisterFormData) => {
  const res = await apiCall('/auth/register', {
    method: "POST",
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

  const data = await res.json().catch(()=> null);

  if (!res.ok) {
    throw new Error(data?.message || "Registration failed");
  }
  if (data?.token) {
    localStorage.setItem("token", data.token);
    
    // Store user info for UI
    const user = getCurrentUser();
    if (user) {
      localStorage.setItem('user', JSON.stringify({
        id: user.userId,
        role: user.role,
        email: formData.email
      }));
    }
  }


  return data;
};
