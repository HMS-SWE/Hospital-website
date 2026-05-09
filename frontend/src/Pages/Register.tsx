import Input from '../Components/Input/input'
import CheckBox from '../CheckBox';
import Styles from "./Register.module.css"
import { useState } from "react";
import { validateRegister } from "./RegisterValidation";
import { register } from '../Components/auth';
import { Link, useNavigate } from "react-router-dom";

export type RegisterFormData = {
    firstName: string;
    middleName: string;
    lastName: string;
    nationalId: string;
    dob: string;
    gender: string;
    email: string;
    phone: string;
    emergency: string;
    password: string;
    confirmPassword: string;
}

const handleOAuthLogin = (provider: 'google') => {
    window.location.href = `http://localhost:8080/oauth2/authorization/${provider}`;
};

function Register() {
    const [errors, setErrors] = useState<
        Partial<Record<keyof RegisterFormData, string>>
    >({});

    const [serverError, setServerError] = useState("");
    const [success, setSuccess] = useState("");
    const [showPassword, setShowPassword] = useState(false);
    const [showConfirmPassword, setShowConfirmPassword] = useState(false);
    const navigate = useNavigate();

    const handleSubmit = async (e: React.FormEvent) => {
        setServerError("");
        setSuccess("");
        setErrors({});

        e.preventDefault();
        const validationErrors = validateRegister(formData);

        if (Object.keys(validationErrors).length > 0) {
            setErrors(validationErrors);
            return;
        }

        try {
            await register(formData);

            setSuccess("Registration successful!");
            setFormData({
                firstName: "",
                middleName: "",
                lastName: "",
                nationalId: "",
                dob: "",
                gender: "Male",
                email: "",
                phone: "",
                emergency: "",
                password: "",
                confirmPassword: "",
            });
            navigate("/");

        } catch (err: unknown) {
            const message = err instanceof Error ? err.message : "Registration failed";
            setServerError(message);
            setSuccess("");
        }

    };

    const [formData, setFormData] = useState<RegisterFormData>({
        firstName: "",
        middleName: "",
        lastName: "",
        nationalId: "",
        dob: "",
        gender: "Male",
        email: "",
        phone: "",
        emergency: "",
        password: "",
        confirmPassword: "",
    });

    const today = new Date().toISOString().split("T")[0];

    return (
        <>
            <div className={Styles.Square}>

                <div className={Styles.LHS}>
                    <div className={Styles.HospitalHeadline}>
                        <h1>Hospital Management System</h1>
                        <span>Streamline your healthcare operations with our comprehensive patient management platform</span>
                    </div>
                </div>

                <div className={Styles.RHS}>
                    <div className={Styles.Headline}>
                        <h1>Patient Registration</h1>
                        <span>Create your account</span>
                    </div>
                    <div className={Styles.Data}>
                        {serverError && <p className={Styles.error}>{serverError}</p>}
                        {success && <p className={Styles.success}>{success}</p>}
                        <form onSubmit={handleSubmit}>
                            <div className={Styles.Name}>
                                <Input label="First name:*"
                                       type="text"
                                       value={formData.firstName}
                                       onChange={(e) => setFormData({ ...formData, firstName: e.target.value })}
                                       error={errors.firstName}
                                />

                                <Input label="Middle name:*"
                                       type="text"
                                       value={formData.middleName}
                                       onChange={(e) => setFormData({ ...formData, middleName: e.target.value })}
                                       error={errors.middleName}
                                />

                                <Input label="Last name:*"
                                       type="text"
                                       value={formData.lastName}
                                       onChange={(e) => setFormData({ ...formData, lastName: e.target.value })}
                                       error={errors.lastName}
                                />
                            </div>
                            <div className={Styles.IDDOB}>

                                <Input label="National ID:*"
                                       type="text"
                                       value={formData.nationalId}
                                       onChange={(e) => setFormData({ ...formData, nationalId: e.target.value })}
                                       error={errors.nationalId}
                                       inputProps={{ inputMode: "numeric", maxLength: 14 }}
                                />

                                <Input label="Date of Birth:*"
                                       type="date"
                                       value={formData.dob}
                                       onChange={(e) => setFormData({ ...formData, dob: e.target.value })}
                                       error={errors.dob}
                                       inputProps={{ max: today }}
                                />
                            </div>
                            <div className={Styles.genderField}>
                                <label>Gender:*</label>
                                <br></br>
                                <select className={Styles.gender}
                                        value={formData.gender}
                                        onChange={(e) => setFormData({ ...formData, gender: e.target.value })}
                                >
                                    <option>Male</option>
                                    <option>Female</option>
                                </select>
                            </div>
                            <div className={Styles.mailphone}>
                                <Input label="Email:*"
                                       type="text"
                                       value={formData.email}
                                       onChange={(e) => setFormData({ ...formData, email: e.target.value })}
                                       error={errors.email}
                                />

                                <Input label="Phone Number:*"
                                       type="text"
                                       value={formData.phone}
                                       onChange={(e) => setFormData({ ...formData, phone: e.target.value })}
                                       error={errors.phone}
                                />

                            </div>
                            <div className={Styles.EmContact}>
                                <Input label="Emergency Contact:*"
                                       type="text"
                                       value={formData.emergency}
                                       onChange={(e) => setFormData({ ...formData, emergency: e.target.value })}
                                       error={errors.emergency}
                                />

                            </div>
                            <div className={Styles.Pass}>
                                <div className={Styles.passwordField}>
                                    <label>Password:*</label>
                                    <div className={Styles.passwordInputWrapper}>
                                        <input
                                            className={Styles.passwordInput}
                                            type={showPassword ? "text" : "password"}
                                            value={formData.password}
                                            onChange={(e) => setFormData({ ...formData, password: e.target.value })}
                                            autoComplete="new-password"
                                        />
                                        <button
                                            type="button"
                                            className={Styles.toggleButton}
                                            onClick={() => setShowPassword((prev) => !prev)}
                                            aria-label={showPassword ? "Hide password" : "Show password"}
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
                                    {errors.password && <span style={{ color: "red" }}>{errors.password}</span>}
                                </div>
                                <div className={Styles.passwordField}>
                                    <label>Confirm Password:*</label>
                                    <div className={Styles.passwordInputWrapper}>
                                        <input
                                            className={Styles.passwordInput}
                                            type={showConfirmPassword ? "text" : "password"}
                                            value={formData.confirmPassword}
                                            onChange={(e) => setFormData({ ...formData, confirmPassword: e.target.value })}
                                            autoComplete="new-password"
                                        />
                                        <button
                                            type="button"
                                            className={Styles.toggleButton}
                                            onClick={() => setShowConfirmPassword((prev) => !prev)}
                                            aria-label={showConfirmPassword ? "Hide password" : "Show password"}
                                        >
                                            {showConfirmPassword ? (
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
                                    {errors.confirmPassword && <span style={{ color: "red" }}>{errors.confirmPassword}</span>}
                                </div>
                            </div>
                            <div className={Styles.checklist}>
                                <span>Chronic disease (if any):</span>
                                <div className={Styles.checks}>
                                    <div><CheckBox name="diabetes" title="Diabetes" /></div>
                                    <div ><CheckBox name="heart" title="Heart disease" /></div>
                                    <div><CheckBox name="pressure" title="Blood pressure disease" /></div>
                                    <div><CheckBox name="immune" title="Immune system disease" /></div>
                                </div>
                            </div>
                            <button type='submit' className={Styles.submitButton}>Register</button>
                        </form>
                        <div className={Styles.divider}>Or continue with:</div>
                        <button type='button' className={Styles.googleButton} onClick={() => handleOAuthLogin('google')}>
                            <div className={Styles.googleIcon} >
                                <img className={Styles.Icon} src='./icons8-google.svg' alt="User Icon" />
                            </div>
                            <div className={Styles.ButtonText}>
                                Register with google
                            </div>
                        </button>
                        <h5>Already have an account?<Link to="/">Login</Link></h5>
                    </div>

                </div>
            </div>

        </>
    );
}
export default Register