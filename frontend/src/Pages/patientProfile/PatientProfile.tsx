import { useEffect,useState } from 'react';
import Input from '../../Components/Input/input' 
import Styles from './PatientProfile.module.css'
function PatientProfile(){
    const [profile,setProfile] = useState<any>(null);
    const today = new Date().toISOString().split("T")[0];
    const [showPassword, setShowPassword] = useState(false);
    const [form, setForm]= useState({
        firstName: "",
        middleName: "",
        lastName: "",
        email: "",
        phone: "",
        nationalId: "",
        dateOfBirth: "",
    });

    useEffect(() => {
    const user = JSON.parse(localStorage.getItem("user") || "{}");

    console.log("USER:", user);

    if (!user?.id || !user?.token) {
        console.log("Missing user id or token");
        return;
    }

    fetch(`http://localhost:8080/api/patients/${user.id}/profile`, {
        method: "GET",
        headers: {
            Authorization: `Bearer ${user.token}`,
            "Content-Type": "application/json",
        },
    })
        .then(async (res) => {
            console.log("STATUS:", res.status);

            const text = await res.text();
            console.log("RAW RESPONSE:", text);

            if (!res.ok) throw new Error(text);

            return JSON.parse(text);
        })
        .then(data => {
            console.log("PROFILE DATA:", data);
            setProfile(data);
        })
        .catch(err => console.error("FETCH ERROR:", err));
    }, []);
    useEffect(() => {
        if (!profile) return;

        setForm({
            firstName: profile.firstName || "",
            middleName: profile.middleName || "",
            lastName: profile.lastName || "",
            email: profile.email || "",
            phone: profile.phone || "",
            nationalId: profile.nationalId || "",
            dateOfBirth: profile.dateOfBirth || "",
        });
    }, [profile]);

    const handleCancel = () => {
        if (!profile) return;

        setForm({
            firstName: profile.firstName || "",
            middleName: profile.middleName || "",
            lastName: profile.lastName || "",
            email: profile.email || "",
            phone: profile.phone || "",
            nationalId: profile.nationalId || "",
            dateOfBirth: profile.dateOfBirth || "",
        });
    };
    const handleSave = () => {
    const user = JSON.parse(localStorage.getItem("user") || "{}");

        fetch(`http://localhost:8080/api/patients/${user.id}/profile`, {
            method: "PUT",
            headers: {
                Authorization: `Bearer ${user.token}`,
                "Content-Type": "application/json",
            },
            body: JSON.stringify(form),
        })
            .then(res => res.json())
            .then(data => {
                console.log("Updated:", data);
                setProfile(data);
            })
            .catch(err => console.error(err));
    };

    return(
        <>
            <div className={Styles.pProfileContainer}>
                <div className={Styles.pProfileContent}>
                    <div className={Styles.pProfile}>
                        <button className={Styles.changeImg}>Change Image</button>
                        <img className={Styles.pProfileImg}></img>
                        <h1>UserName</h1>
                    </div>
                    <div className={Styles.pDetails}>
                        <form>
                            <div className={Styles.pName}>
                                <Input
                                    label='First Name:'
                                    type='text'
                                    value={form.firstName}
                                    onChange={(e: any) =>
                                        setForm({ ...form, firstName: e.target.value })
                                    }
                                />
                                <Input
                                    label='Middle Name:'
                                    type='text'
                                    value={form.middleName}
                                    onChange={(e: any) =>
                                        setForm({ ...form, middleName: e.target.value })
                                    }
                                />
                                <Input
                                    label='Last Name:'
                                    type='text'
                                    value={form.lastName}
                                    onChange={(e: any) =>
                                        setForm({ ...form, lastName: e.target.value })
                                    }
                                />
                            </div>
                            <div className={Styles.IDDOB}>

                                <Input label="National ID:"
                                       type="text"
                                       value={form.nationalId}
                                       inputProps={{ inputMode: "numeric", maxLength: 14 }}
                                       onChange={(e: any) =>
                                        setForm({ ...form, nationalId: e.target.value })
                                    }
                                />

                                <Input label="Date of Birth:"
                                       type="date"
                                       value={form.dateOfBirth}
                                       inputProps={{ max: today }}
                                       onChange={(e: any) =>
                                        setForm({ ...form, dateOfBirth: e.target.value })
                                    }
                                />
                                <Input label="Email:"
                                       type="text"
                                       value={form.email}
                                       onChange={(e: any) =>
                                        setForm({ ...form, email: e.target.value })
                                    }
                                />

                                <Input label="Phone Number:"
                                       type="text"
                                       value={form.phone}
                                       onChange={(e: any) =>
                                        setForm({ ...form, phone: e.target.value })
                                    }
                                />
                                <div className={Styles.passwordField}>
                                    <label>Password:</label>
                                    <div className={Styles.passwordInputWrapper}>
                                        <input
                                            className={Styles.passwordInput}
                                            type={showPassword ? "text" : "password"}
                                            value="TestPassword"
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
                                </div>
                                <Input label="Emergency Contact:*"
                                       type="text"
                                       value="01220020407"
                                />
                            </div>
                            <div className={Styles.pButtons}>
                                        <button className={Styles.saveButton} onClick={handleSave}>Save Changes</button>
                                        <button className={Styles.cancelButton} onClick={handleCancel}>Cancel</button>
                            </div>
                            
                        </form>
                        
                    </div>
                </div>
            </div>
        </>
    );
}
export default PatientProfile;