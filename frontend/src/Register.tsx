import Input from './input'
import Button from './Button/Button'
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faGoogle } from '@fortawesome/free-brands-svg-icons';

function Register(){
    return(
        <>
        <div className='Container'>
            <div className='LHS'>
                <h1 className='RegisterMessage'>Welcome to our Hospital System</h1>
            </div>
            <div className='RHS'>
                <h2>Please fill-in your details</h2>
                <div className='Data'>
                    <Input label="First name:" type="text" />
                    <div className="Field">
                        <label>Gender:</label>
                        <br></br>
                        <select className='gender'>
                        <option>Male</option>
                        <option>Female</option>
                        </select>
                    </div>
                    <Input label="Middle name:" type="text" />
                    <Input label="Phone number:" type="text" />
                    <Input label="Last name:" type="text" />
                    <Input label="E-mail:" type="email" />
                    <Input label="Date of Birth:" type="date"/>
                    <Input label="Password:" type="password" />
                </div>
                <Button />
                <span>Or register using: 
                    <a href='#'><FontAwesomeIcon icon={faGoogle} /></a>
                </span>
            </div>
            </div>  
        </>
    );
}
export default Register