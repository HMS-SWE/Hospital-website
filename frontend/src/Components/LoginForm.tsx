import { useState, useMemo, type FormEvent } from 'react'
import { useNavigate } from 'react-router-dom'
import { login } from './auth'

type LoginFormData = {
  email: string
  password: string
}

const emailPattern = /^[^\s@]+@[^\s@]+\.[^\s@]+$/

export function LoginForm() {
  const navigate = useNavigate()
  const [formData, setFormData] = useState<LoginFormData>({ email: '', password: '' })
  const [errors, setErrors] = useState<Partial<Record<keyof LoginFormData, string>>>({})
  const [generalError, setGeneralError] = useState('')
  const [submitted, setSubmitted] = useState(false)

  const handleOAuthLogin = (provider: 'google') => {
    window.location.href = `http://localhost:8080/oauth2/authorization/${provider}`
  }

  const validationErrors = useMemo(() => {
    const next: Partial<Record<keyof LoginFormData, string>> = {}

    if (!formData.email.trim()) {
      next.email = 'Email address is required'
    } else if (!emailPattern.test(formData.email.trim())) {
      next.email = 'Enter a valid email address'
    }

    if (!formData.password.trim()) {
      next.password = 'Password is required'
    }

    return next
  }, [formData])

  const handleChange = (field: keyof LoginFormData, value: string) => {
    setFormData(prev => ({ ...prev, [field]: value }))
    setErrors(prev => ({ ...prev, [field]: undefined }))
    setGeneralError('')
  }

  const handleSubmit = async (e: FormEvent<HTMLFormElement>) => {
    e.preventDefault()
    setSubmitted(true)

    const errs = validationErrors
    setErrors(errs)
    if (Object.keys(errs).length > 0) return

    try {
      const result = await login({
        email: formData.email.trim(),
        password: formData.password,
      })

      if (result.role === 'ADMIN') navigate('/admin')
      else if (result.role === 'DOCTOR') navigate('/doctor')
      else navigate('/patient')
    } catch (error) {
      setGeneralError(error instanceof Error ? error.message : 'Login failed')
    }
  }

  return (
      <form onSubmit={handleSubmit}>
        <input
            type="email"
            value={formData.email}
            onChange={e => handleChange('email', e.target.value)}
            placeholder="Email"
        />
        {submitted && errors.email && <p>{errors.email}</p>}

        <input
            type="password"
            value={formData.password}
            onChange={e => handleChange('password', e.target.value)}
            placeholder="Password"
        />
        {submitted && errors.password && <p>{errors.password}</p>}

        {generalError && <p>{generalError}</p>}

        <button type="submit">Sign In</button>
        <button type="button" onClick={() => handleOAuthLogin('google')}>
          Sign in with Google
        </button>
      </form>
  )
}
