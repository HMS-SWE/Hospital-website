import { useState } from 'react';
import Input from '../../Components/Input/input' 
import Styles from './PatientProfile.module.css'

function PatientProfile(){
    const today = new Date().toISOString().split("T")[0];
    const [showPassword, setShowPassword] = useState(false);

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
                                    value='Ahmed'
                                />
                                <Input
                                    label='Middle Name:'
                                    type='text'
                                    value='Mohamed'
                                />
                                <Input
                                    label='Last Name:'
                                    type='text'
                                    value='Ali'
                                />
                            </div>
                            <div className={Styles.IDDOB}>

                                <Input label="National ID:"
                                       type="text"
                                       value="30409301615272"
                                       inputProps={{ inputMode: "numeric", maxLength: 14 }}
                                />

                                <Input label="Date of Birth:"
                                       type="date"
                                       value="05/09/2004"
                                       inputProps={{ max: today }}
                                />
                                <Input label="Email:"
                                       type="text"
                                       value="TestEmail@gmail.com"
                                />

                                <Input label="Phone Number:"
                                       type="text"
                                       value="01204006126"
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
                                        <button className={Styles.saveButton}>Save Changes</button>
                                        <button className={Styles.cancelButton}>Cancel</button>
                            </div>
                            
                        </form>
                        
                    </div>
                </div>
            </div>
        </>
    );
}
export default PatientProfile;