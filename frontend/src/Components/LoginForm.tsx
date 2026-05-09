import { useState, useMemo, type FormEvent } from 'react'
import Styles from './LoginForm.module.css'
import { useNavigate } from 'react-router-dom';
import { login, getProfile } from './auth'

type LoginFormData = {
  email: string
  password: string
}

const emailPattern = /^[^\s@]+@[^\s@]+\.[^\s@]+$/

export function LoginForm() {
  const handleOAuthLogin = (provider: 'google') => {
    window.location.href = `http://localhost:8080/oauth2/authorization/${provider}`;
  };
  
  const navigate = useNavigate();
  const [formData, setFormData] = useState<LoginFormData>({ email: '', password: '' })
  const [errors, setErrors] = useState<Partial<Record<keyof LoginFormData, string>>>({})
  const [generalError, setGeneralError] = useState('')
  const [submitted, setSubmitted] = useState(false)
  const [showPassword, setShowPassword] = useState(false)

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
      const data = await login(formData.email, formData.password);

      const profile = data.token ? await getProfile(data.role, data.token) : null;
      const user = {
        id: profile?.id ?? 'unknown',
        role: data.role,
        email: formData.email,
        name: profile?.fullName ?? profile?.userName ?? formData.email,
      };
      
      localStorage.setItem('user', JSON.stringify(user));
      window.dispatchEvent(new Event("authChange"));

      alert(`Signed in as ${data.role}`);
      navigate('/dashboard');

    } catch (error) {
      console.error("Login failed:", error);
      setGeneralError(error instanceof Error ? error.message : 'Server unreachable. Please try again later.');
    }
  }

  return (
    <div className={Styles.page}>
      <div className={Styles.card}>

        {/* Left panel */}
        <div className={Styles.left}>
          <div>
            <div className={Styles.logo_box}>
              <svg width="22" height="22" fill="none" stroke="white" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" viewBox="0 0 24 24">
                <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z" />
                <polyline points="14 2 14 8 20 8" />
                <line x1="12" y1="11" x2="12" y2="17" />
                <line x1="9" y1="14" x2="15" y2="14" />
              </svg>
            </div>
            <h2 className={Styles.left_title}>Hospital Management System</h2>
            <p className={Styles.left_desc}>
              Streamline your healthcare operations with our comprehensive patient management platform
            </p>
          </div>

          <ul className={Styles.feature_list}>
            {['Secure patient data management', 'Easy appointment scheduling', 'Real-time collaboration'].map(f => (
              <li key={f} className={Styles.feature_item}>
                <div className={Styles.check_circle}>
                  <svg width="12" height="12" fill="none" stroke="white" strokeWidth="2.5" strokeLinecap="round" strokeLinejoin="round" viewBox="0 0 24 24">
                    <polyline points="20 6 9 17 4 12" />
                  </svg>
                </div>
                {f}
              </li>
            ))}
          </ul>
        </div>

        {/* Right panel */}
        <div className={Styles.right}>
          <h1 className={Styles.heading}>Welcome Back</h1>
          <p className={Styles.subheading}>Sign in to access your dashboard</p>

          {generalError && (
            <div className={Styles.general_error}>{generalError}</div>
          )}

          <form onSubmit={handleSubmit} noValidate className={Styles.form}>

            {/* Email */}
            <div className={Styles.field}>
              <label htmlFor="email" className={Styles.label}>Email Address</label>
              <div className={`${Styles.input_wrap} ${submitted && errors.email ? Styles.error : ''}`}>
                <span className={Styles.input_icon}>
                  <svg width="16" height="16" fill="none" stroke="currentColor" strokeWidth="1.8" strokeLinecap="round" strokeLinejoin="round" viewBox="0 0 24 24">
                    <path d="M4 4h16c1.1 0 2 .9 2 2v12c0 1.1-.9 2-2 2H4c-1.1 0-2-.9-2-2V6c0-1.1.9-2 2-2z" />
                    <polyline points="22,6 12,13 2,6" />
                  </svg>
                </span>
                <input
                  id="email"
                  type="email"
                  placeholder="Enter your email"
                  autoComplete="email"
                  value={formData.email}
                  onChange={e => handleChange('email', e.target.value)}
                />
              </div>
              {submitted && errors.email && (
                <p className={Styles.field_error}>{errors.email}</p>
              )}
            </div>

            {/* Password */}
            <div className={Styles.field}>
              <label htmlFor="password" className={Styles.label}>Password</label>
              <div className={`${Styles.input_wrap} ${submitted && errors.password ? Styles.error : ''}`}>
                <span className={Styles.input_icon}>
                  <svg width="16" height="16" fill="none" stroke="currentColor" strokeWidth="1.8" strokeLinecap="round" strokeLinejoin="round" viewBox="0 0 24 24">
                    <rect x="3" y="11" width="18" height="11" rx="2" ry="2" />
                    <path d="M7 11V7a5 5 0 0 1 10 0v4" />
                  </svg>
                </span>
                <input
                  id="password"
                  type={showPassword ? 'text' : 'password'}
                  placeholder="Enter your password"
                  autoComplete="current-password"
                  value={formData.password}
                  onChange={e => handleChange('password', e.target.value)}
                />
                <button
                  type="button"
                  className={Styles.toggle_pw}
                  onClick={() => setShowPassword(p => !p)}
                  aria-label={showPassword ? 'Hide password' : 'Show password'}
                >
                  {showPassword ? (
                    <svg width="16" height="16" fill="none" stroke="currentColor" strokeWidth="1.8" strokeLinecap="round" strokeLinejoin="round" viewBox="0 0 24 24">
                      <path d="M17.94 17.94A10.07 10.07 0 0 1 12 20c-7 0-11-8-11-8a18.45 18.45 0 0 1 5.06-5.94" />
                      <path d="M9.9 4.24A9.12 9.12 0 0 1 12 4c7 0 11 8 11 8a18.5 18.5 0 0 1-2.16 3.19" />
                      <line x1="1" y1="1" x2="23" y2="23" />
                    </svg>
                  ) : (
                    <svg width="16" height="16" fill="none" stroke="currentColor" strokeWidth="1.8" strokeLinecap="round" strokeLinejoin="round" viewBox="0 0 24 24">
                      <path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z" />
                      <circle cx="12" cy="12" r="3" />
                    </svg>
                  )}
                </button>
              </div>
              {submitted && errors.password && (
                <p className={Styles.field_error}>{errors.password}</p>
              )}
            </div>

            {/* Remember me / Forgot password */}
            <div className={Styles.options_row}>
              <label className={Styles.remember_label}>
                <input type="checkbox" />
                Remember me
              </label>
              <a href="#forgot" className={Styles.forgot_link}>Forgot password?</a>
            </div>

            {/* Submit */}
            <button type="submit" className={Styles.submit_btn}>Sign In</button>

            {/* Divider */}
            <div className={Styles.divider}>
              <div className={Styles.divider_line} />
              <span>Or continue with</span>
              <div className={Styles.divider_line} />
            </div>

            {/* Google */}
            <button type="button" className={Styles.google_btn} onClick={() => handleOAuthLogin('google')}>
              <svg width="17" height="17" viewBox="0 0 24 24">
                <path fill="#4285F4" d="M22.56 12.25c0-.78-.07-1.53-.2-2.25H12v4.26h5.92c-.26 1.37-1.04 2.53-2.21 3.31v2.77h3.57c2.08-1.92 3.28-4.74 3.28-8.09z" />
                <path fill="#34A853" d="M12 23c2.97 0 5.46-.98 7.28-2.66l-3.57-2.77c-.98.66-2.23 1.06-3.71 1.06-2.86 0-5.29-1.93-6.16-4.53H2.18v2.84C3.99 20.53 7.7 23 12 23z" />
                <path fill="#FBBC05" d="M5.84 14.09c-.22-.66-.35-1.36-.35-2.09s.13-1.43.35-2.09V7.07H2.18C1.43 8.55 1 10.22 1 12s.43 3.45 1.18 4.93l3.66-2.84z" />
                <path fill="#EA4335" d="M12 5.38c1.62 0 3.06.56 4.21 1.64l3.15-3.15C17.45 2.09 14.97 1 12 1 7.7 1 3.99 3.47 2.18 7.07l3.66 2.84c.87-2.6 3.3-4.53 6.16-4.53z" />
              </svg>
              Sign in with Google
            </button>

            <p className={Styles.signup_text}>
              Don't have an account? <a href="#register" onClick={() => navigate("/register")}>Sign up</a>
            </p>

          </form>
        </div>

      </div>
    </div>
  )
}