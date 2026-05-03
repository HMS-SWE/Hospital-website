import type { UserRole } from './auth'
import { Button } from './Button'

type DashboardUser = {
  email: string
  role: UserRole
}

interface DashboardProps {
  user: DashboardUser
  onLogout: () => void
}

const titleMap: Record<UserRole, string> = {
  PATIENT: 'Patient Dashboard',
  DOCTOR: 'Doctor Dashboard',
  ADMIN: 'Admin Dashboard',
}

const descriptionMap: Record<UserRole, string> = {
  PATIENT: 'View your appointments, medical record summaries, and recent notifications.',
  DOCTOR: 'Manage your schedule, patient cases, and consultation requests.',
  ADMIN: 'Access system controls, user management, and hospital reports.',
}

export function Dashboard({ user, onLogout }: DashboardProps) {
  return (
      <section className="dashboard-card">
        <div className="dashboard-header">
          <div>
            <p className="eyebrow">{user.role}</p>
            <h1>{titleMap[user.role]}</h1>
            <p className="login-copy">{descriptionMap[user.role]}</p>
          </div>
          <Button type="button" variant="secondary" onClick={onLogout}>
            Logout
          </Button>
        </div>
        <div className="dashboard-body">
          <p>Welcome back, <strong>{user.email}</strong>.</p>
          <p>Redirected to <code>/{user.role.toLowerCase()}</code> for your role-specific dashboard.</p>
        </div>
      </section>
  )
}
