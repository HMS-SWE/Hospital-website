import { useMemo, useState } from 'react'
import type { FormEvent } from 'react'
import { Button } from './Button'
import { Input } from './Input'
import type { LoginCredentials } from '../auth'

export type LoginFormData = {
  emailOrUsername: string
  password: string
}

const roles = [
  { label: 'Patient', value: 'patient' },
  { label: 'Doctor', value: 'doctor' },
  { label: 'Admin', value: 'admin' },
] as const

const emailPattern = /^[^\s@]+@[^\s@]+\.[^\s@]+$/

interface LoginFormProps {
  onLogin: (credentials: LoginCredentials) => boolean
}

export function LoginForm({ onLogin }: LoginFormProps) {
  const [role, setRole] = useState<typeof roles[number]>(roles[0])
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

    const success = onLogin({
      emailOrUsername: formData.emailOrUsername,
      password: formData.password,
      role: role.value,
    })

    if (!success) {
      setGeneralError('Invalid email or password')
      return
    }

    setGeneralError('')
  }

  return (
    <div className="login-page">
      <div className="login-card">
        <div className="login-header">
          <p className="eyebrow">Hospital Management</p>
          <h1>Sign in to your account</h1>
          <p className="login-copy">Choose your role and enter your credentials to continue.</p>
        </div>

        <div className="role-tabs" role="tablist" aria-label="Select user role">
          {roles.map(currentRole => (
            <button
              key={currentRole.value}
              type="button"
              className={`role-tab ${role.value === currentRole.value ? 'role-tab--active' : ''}`}
              onClick={() => setRole(currentRole)}
              role="tab"
              aria-selected={role.value === currentRole.value}
            >
              {currentRole.label}
            </button>
          ))}
        </div>

        <form className="login-form" onSubmit={handleSubmit} noValidate>
          <Input
            id="emailOrUsername"
            label="Email or Username"
            value={formData.emailOrUsername}
            placeholder="Enter your email or username"
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

          {generalError ? <div className="general-error">{generalError}</div> : null}

          <Button type="submit">Login</Button>

          <div className="login-footer">
            <a className="login-footer-link" href="#forgot-password">
              Forgot password?
            </a>
            <p className="register-text">
              Don’t have an account?{' '}
              <a className="login-footer-link" href="#register">
                Register
              </a>
            </p>
          </div>
        </form>
      </div>
    </div>
  )
}
