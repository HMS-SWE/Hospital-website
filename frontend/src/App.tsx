import { useEffect, useState } from 'react'
import './App.css'
import { LoginForm } from './components/LoginForm'
import { Dashboard } from './components/Dashboard'
import type { LoginCredentials, User } from './auth'
import { authenticate } from './auth'

const allowedRoutes = ['/patient', '/doctor', '/admin']

function normalizeRoute(path: string) {
  return allowedRoutes.includes(path.toLowerCase()) ? path.toLowerCase() : '/'
}

function loadSessionUser(): User | null {
  const stored = sessionStorage.getItem('hospital-session')
  if (!stored) return null

  try {
    return JSON.parse(stored) as User
  } catch {
    return null
  }
}

function App() {
  const [user, setUser] = useState<User | null>(() => loadSessionUser())
  const [route, setRoute] = useState(() => normalizeRoute(window.location.pathname))

  useEffect(() => {
    const handlePopState = () => {
      setRoute(normalizeRoute(window.location.pathname))
    }

    window.addEventListener('popstate', handlePopState)
    return () => window.removeEventListener('popstate', handlePopState)
  }, [])

  useEffect(() => {
    if (user) {
      const targetRoute = `/${user.role}`
      if (route !== targetRoute) {
        window.history.replaceState({}, '', targetRoute)
        setRoute(targetRoute)
      }
    }
  }, [route, user])

  const handleLogin = (credentials: LoginCredentials) => {
    try {
      const authenticated = authenticate(credentials)
      if (!authenticated) {
        return false
      }

      setUser(authenticated)
      sessionStorage.setItem('hospital-session', JSON.stringify(authenticated))
      const targetRoute = `/${authenticated.role}`
      window.history.pushState({}, '', targetRoute)
      setRoute(targetRoute)
      return true
    } catch (error) {
      console.error('Login error:', error)
      return false
    }
  }

  const handleLogout = () => {
    try {
      setUser(null)
      sessionStorage.removeItem('hospital-session')
      window.history.pushState({}, '', '/')
      setRoute('/')
    } catch (error) {
      console.error('Logout error:', error)
      setUser(null)
    }
  }

  const canViewDashboard = user !== null && route === `/${user.role}`

  return (
    <main className="app-shell">
      {canViewDashboard ? (
        <Dashboard user={user} onLogout={handleLogout} />
      ) : (
        <LoginForm onLogin={handleLogin} />
      )}
    </main>
  )
}

export default App
