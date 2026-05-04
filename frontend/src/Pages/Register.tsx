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
    });
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
                                       onChange={
                                           (e) => setFormData({ ...formData, firstName: e.target.value })
                                       }
                                       error={errors.firstName}
                                />


                                <Input label="Middle name:*"
                                       type="text"
                                       value={formData.middleName}
                                       onChange={
                                           (e) => setFormData({ ...formData, middleName: e.target.value })
                                       }
                                       error={errors.middleName}
                                />

                                <Input label="Last name:*"
                                       type="text"
                                       value={formData.lastName}
                                       onChange={
                                           (e) => setFormData({ ...formData, lastName: e.target.value })
                                       }
                                       error={errors.lastName}
                                />
                            </div>
                            <div className={Styles.IDDOB}>

                                <Input label="National ID:*"
                                       type="text"
                                       value={formData.nationalId}
                                       onChange={
                                           (e) => setFormData({ ...formData, nationalId: e.target.value })
                                       }
                                       error={errors.nationalId}
                                />

                                <Input label="Date of Birth:*"
                                       type="date"
                                       value={formData.dob}
                                       onChange={
                                           (e) => setFormData({ ...formData, dob: e.target.value })
                                       }
                                       error={errors.dob}
                                />
                            </div>
                            <div className={Styles.genderField}>
                                <label>Gender:*</label>
                                <br></br>
                                <select className={Styles.gender}
                                        value={formData.gender}
                                        onChange={(e) =>
                                            setFormData({ ...formData, gender: e.target.value })}
                                >
                                    <option>Male</option>
                                    <option>Female</option>
                                </select>
                            </div>
                            <div className={Styles.mailphone}>
                                <Input label="Email:*"
                                       type="text"
                                       value={formData.email}
                                       onChange={
                                           (e) => setFormData({ ...formData, email: e.target.value })
                                       }
                                       error={errors.email}
                                />

                                <Input label="Phone Number:*"
                                       type="text"
                                       value={formData.phone}
                                       onChange={
                                           (e) => setFormData({ ...formData, phone: e.target.value })
                                       }
                                       error={errors.phone}
                                />

                            </div>
                            <div className={Styles.EmContact}>
                                <Input label="Emergency Contact:*"
                                       type="text"
                                       value={formData.emergency}
                                       onChange={
                                           (e) => setFormData({ ...formData, emergency: e.target.value })
                                       }
                                       error={errors.emergency}
                                />

                            </div>
                            <div className={Styles.Pass}>
                                <Input label="Password:*"
                                       type="password"
                                       value={formData.password}
                                       onChange={
                                           (e) => setFormData({ ...formData, password: e.target.value })
                                       }
                                       error={errors.password}
                                />
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