import Input from '../Components/Input/input'
import CheckBox from '../CheckBox';
import  Styles  from './Register.module.css'

function Register(){
    return(
        <>
        <div className= {Styles.Square}>

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
                <div className={Styles.Name}>
                    <Input label="First name:*" type="text" />
                    <Input label="Middle name:*" type="text" />
                    <Input label="Last name:*" type="text" />
                </div>
                <div className={Styles.IDDOB}>
                    <Input label="National ID:*" type="text" />
                    <Input label="Date of Birth:*" type="date" />
                </div>
                <div className={Styles.genderField}>
                        <label>Gender:*</label>
                        <br></br>
                        <select className={Styles.gender}>
                        <option>Male</option>
                        <option>Female</option>
                        </select>
                </div>
                <div className={Styles.mailphone}>
                    <Input label="Email:*" type="text" />
                    <Input label="Phone Number:*" type="text" />
                </div>
                <div className={Styles.EmContact}>
                    <Input label="Emergency Contact:*" type="text" />
                </div>
                <div className={Styles.Pass}>
                    <Input label="Password:*" type="password" />
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
                <button className={Styles.submitButton}>Register</button>
                <div className={Styles.divider}>Or continue with:</div>
                <button className={Styles.googleButton}>
                    <div className={Styles.googleIcon} >
                    <img className={Styles.Icon} src='./icons8-google.svg' alt="User Icon" />
                    </div>
                    <div className={Styles.ButtonText}>
                        Register with google
                    </div>
                    </button>
                <h5>Already have an account?<a href='#'>Login</a></h5>
            </div>
            
            </div>
        </div>
        
        
        </>
    );
}
export default Register