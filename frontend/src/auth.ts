export type User = {
  email: string
  password: string
  role: 'patient' | 'doctor' | 'admin'
}

export type LoginCredentials = {
  emailOrUsername: string
  password: string
  role: User['role']
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
        user.password === credentials.password &&
        user.role === credentials.role,
    ) || null
  )
}
