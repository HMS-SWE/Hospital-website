import type { User } from '../auth'
import { Button } from './Button'

interface DashboardProps {
  user: User
  onLogout: () => void
}

const titleMap: Record<User['role'], string> = {
  patient: 'Patient Dashboard',
  doctor: 'Doctor Dashboard',
  admin: 'Admin Dashboard',
}

const descriptionMap: Record<User['role'], string> = {
  patient: 'View your appointments, medical record summaries, and recent notifications.',
  doctor: 'Manage your schedule, patient cases, and consultation requests.',
  admin: 'Access system controls, user management, and hospital reports.',
}

export function Dashboard({ user, onLogout }: DashboardProps) {
  return (
    <section className="dashboard-card">
      <div className="dashboard-header">
        <div>
          <p className="eyebrow">{user.role.toUpperCase()}</p>
          <h1>{titleMap[user.role]}</h1>
          <p className="login-copy">{descriptionMap[user.role]}</p>
        </div>
        <Button type="button" variant="secondary" onClick={onLogout}>
          Logout
        </Button>
      </div>
      <div className="dashboard-body">
        <p>Welcome back, <strong>{user.email}</strong>.</p>
        <p>Redirected to <code>/{user.role}</code> for your role-specific dashboard.</p>
      </div>
    </section>
  )
}
