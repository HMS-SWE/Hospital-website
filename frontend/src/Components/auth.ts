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
  window.location.href = "/";
};


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

  const data = await res.json().catch(() => null);

  if (!res.ok) {
    throw new Error(data?.message || "Registration failed");
  }
  if (data?.token) {
    localStorage.setItem("token", data.token);
  }


  return data;
};

export type LoginResponse = {
  token?: string
  role: string
  expiresIn?: number
}

export type UserProfile = {
  id: number
  userName?: string
  fullName?: string
  email?: string
  role?: string
}

const normalizeBase64 = (value: string) => {
  const padded = value.padEnd(value.length + (4 - (value.length % 4)) % 4, '=');
  return padded.replace(/-/g, '+').replace(/_/g, '/');
}

const decodeJwtPayload = (token: string) => {
  const parts = token.split('.')
  if (parts.length !== 3) {
    throw new Error('Invalid JWT token')
  }
  const payload = parts[1]
  const decoded = atob(normalizeBase64(payload))
  return JSON.parse(decoded)
}

export const getProfile = async (role: string, token: string) => {
  const normalizedRole = role.toUpperCase()
  const payload = decodeJwtPayload(token)
  const userId = payload.sub

  let profilePath = '/admin/'
  if (normalizedRole === 'PATIENT') {
    profilePath = `/patients/${userId}/profile`
  } else if (normalizedRole === 'DOCTOR') {
    profilePath = `/doctors/${userId}/profile`
  } else {
    profilePath = `/admin/${userId}/profile`
  }

  const res = await fetch(`${API_URL}${profilePath}`, {
    headers: {
      Authorization: `Bearer ${token}`,
    },
  })

  const data = await res.json().catch(() => null)
  if (!res.ok) {
    throw new Error(data?.message || 'Failed to load user profile')
  }

  return data as UserProfile
}

export const login = async (email: string, password: string): Promise<LoginResponse> => {
  const res = await fetch(`${API_URL}/auth/login`, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify({
      email,
      password,
    }),
  });

  const data = await res.json().catch(() => null);

  if (!res.ok) {
    throw new Error(data?.message || "Login failed");
  }
  if (data?.token) {
    localStorage.setItem("token", data.token);
  }

  return data as LoginResponse;
};