import Input from './input'
import Button from './Button/Button'
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faGoogle } from '@fortawesome/free-brands-svg-icons';
import CheckBox from './CheckBox';

function Register(){
    return(
        <>
        <div className='Square'>

            <div className='LHS'>
                <div className='Hospital-Headline'>
                    <h1>Hospital Management System</h1>
                    <span>Streamline your healthcare operations with our comprehensive patient management platform</span>
                </div>
            </div>

            <div className="RHS">
            <div className="Headline">
                <h1>Patient Registration</h1>
                <span>Create your account</span>
            </div>
            <div className="Data">
                <div className="Name">
                    <Input label="First name:" type="text" />
                    <Input label="Middle name:" type="text" />
                    <Input label="Last name:" type="text" />
                </div>
                <div className="ID-DOB">
                    <Input label="National ID:" type="text" />
                    <Input label="Date of Birth:" type="date" />
                </div>
                <div className="genderField">
                        <label>Gender:</label>
                        <br></br>
                        <select className='gender'>
                        <option>Male</option>
                        <option>Female</option>
                        </select>
                </div>
                <div className='mail-phone'>
                    <Input label="Email" type="text" />
                    <Input label="Phone Number" type="text" />
                </div>
                <div className='Em-Contact'>
                    <Input label="Emergency Contact" type="text" />
                </div>
                <div className='Pass'>
                    <Input label="Password" type="password" />
                </div>
                <div className='checklist'>
                    <span>Chronic disease (if any):</span>
                    <div className='checks'>
                        <div><CheckBox name="diabetes" title="Diabetes" /></div>
                        <div ><CheckBox name="heart" title="Heart disease" /></div>
                        <div><CheckBox name="pressure" title="Blood pressure disease" /></div>
                        <div><CheckBox name="immune" title="Immune system disease" /></div>                        
                    </div>
                </div>
                <button className='submitButton'>Register</button>
                <div className='divider'>Or continue with:</div>
                <button className='googleButton'><img className='googleIcon' src='./public/icons8-google.svg' alt="User Icon" />Register with google</button>
                <h5>Already have an account?<a href='#'>Login</a></h5>
            </div>
            
            </div>
        </div>
        
        
        </>
    );
}
export default Register