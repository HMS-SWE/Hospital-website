import { useMemo, useState } from 'react'
import type { FormEvent } from 'react'
import { Button } from './Button'
import { Input } from './Input'
import type { LoginCredentials } from '../auth'

export type LoginFormData = {
  emailOrUsername: string
  password: string
}

const emailPattern = /^[^\s@]+@[^\s@]+\.[^\s@]+$/

const authModes = ['Login', 'Register'] as const
const features = [
  'Secure patient data management',
  'Easy appointment scheduling',
  'Real-time collaboration',
]

interface LoginFormProps {
  onLogin: (credentials: LoginCredentials) => boolean
}

export function LoginForm({ onLogin }: LoginFormProps) {
  const [mode] = useState<typeof authModes[number]>('Login')
  const [rememberMe, setRememberMe] = useState(false)
  const [formData, setFormData] = useState<LoginFormData>({
    emailOrUsername: '',
    password: '',
  })
  const [errors, setErrors] = useState<Partial<Record<keyof LoginFormData, string>>>({})
  const [generalError, setGeneralError] = useState('')
  const [submitted, setSubmitted] = useState(false)

  const validationErrors = useMemo(() => {
    const nextErrors: Partial<Record<keyof LoginFormData, string>> = {}

    if (!formData.emailOrUsername.trim()) {
      nextErrors.emailOrUsername = 'Email or username is required'
    } else if (formData.emailOrUsername.includes('@')) {
      if (!emailPattern.test(formData.emailOrUsername.trim())) {
        nextErrors.emailOrUsername = 'Enter a valid email address'
      }
    }

    if (!formData.password.trim()) {
      nextErrors.password = 'Password is required'
    }

    return nextErrors
  }, [formData])

  const handleFieldChange = (field: keyof LoginFormData, value: string) => {
    setFormData(prev => ({ ...prev, [field]: value }))
    setErrors(prev => ({ ...prev, [field]: undefined }))
    setGeneralError('')
  }

  const handleSubmit = (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault()
    setSubmitted(true)
    const nextErrors = validationErrors
    setErrors(nextErrors)

    if (Object.keys(nextErrors).length > 0) {
      setGeneralError('')
      return
    }

    const credentials: LoginCredentials = {
      emailOrUsername: formData.emailOrUsername,
      password: formData.password,
    }

    const success = onLogin(credentials)
    if (!success) {
      setGeneralError('Invalid email or password')
      return
    }

    setGeneralError('')
  }

  return (
    <div className="login-page">
      <div className="login-grid">
        <section className="login-panel">
          
          <div className="panel-copy">
            <h1 className="panel-title">Hospital Management System</h1>
            <p className="panel-subtitle">Streamline your healthcare operations with our comprehensive patient management platform</p>
            <ul className="feature-list">
              {features.map(feature => (
                <li key={feature} className="feature-item">
                  <span className="feature-mark">✓</span>
                  {feature}
                </li>
              ))}
            </ul>
          </div>
        </section>

        <section className="auth-card">
          <div className="auth-card-header">
            <p className="eyebrow">Welcome Back</p>
            <h2>Sign in to access your dashboard</h2>
          </div>

          <div className="auth-toggle" role="tablist" aria-label="Authentication mode">
            {authModes.map(currentMode => (
              <button
                key={currentMode}
                type="button"
                className={`auth-tab ${mode === currentMode ? 'auth-tab--active' : ''} ${currentMode === 'Register' ? 'auth-tab--disabled' : ''}`}
                onClick={() => {
                  if (currentMode === 'Login') return
                }}
                role="tab"
                aria-selected={mode === currentMode}
                disabled={currentMode === 'Register'}
              >
                {currentMode}
              </button>
            ))}
          </div>

          <form className="login-form" onSubmit={handleSubmit} noValidate>
            <Input
              id="emailOrUsername"
              label="Email Address"
              value={formData.emailOrUsername}
              placeholder="Enter your email"
              onChange={value => handleFieldChange('emailOrUsername', value)}
              error={submitted ? errors.emailOrUsername : undefined}
            />

            <Input
              id="password"
              label="Password"
              type="password"
              value={formData.password}
              placeholder="Enter your password"
              onChange={value => handleFieldChange('password', value)}
              error={submitted ? errors.password : undefined}
            />

            <div className="login-row">
              <label className="checkbox-label">
                <input
                  type="checkbox"
                  checked={rememberMe}
                  onChange={event => setRememberMe(event.target.checked)}
                />
                Remember me
              </label>
              <a className="login-footer-link" href="#forgot-password">
                Forgot password?
              </a>
            </div>

            {generalError ? <div className="general-error">{generalError}</div> : null}

            <Button type="submit">{mode === 'Login' ? 'Login' : 'Create Account'}</Button>

            <div className="divider">Or continue with</div>

            <Button type="button" variant="secondary" className="social-button">
              Sign in with Google
            </Button>

            <p className="register-text">
              Don’t have an account? <button type="button" className="text-button" onClick={() => {}}>Sign up</button>
            </p>
          </form>
        </section>
      </div>
    </div>
  )
}
