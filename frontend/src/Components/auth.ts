import type { RegisterFormData } from "../Pages/Register";

const API_URL = "http://localhost:8080/api";

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
      )||  null
  )
}



export const register = async (formData: RegisterFormData) => {
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

  const data = await res.json().catch(()=> null);

  if (!res.ok) {
    throw new Error(data?.message || "Registration failed");
  }
  if (data?.token) {
    localStorage.setItem("token", data.token);
  }


  return data;
};