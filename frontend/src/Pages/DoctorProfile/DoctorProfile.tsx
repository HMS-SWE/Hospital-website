import Input from '../../Components/Input/input'
import Styles from './DoctorProfile.module.css'
import { useState } from 'react' 

function DoctorProfile(){
    const [phone, setPhone] = useState("01277769853");
    const [price, setPrice] = useState("100 EGP");
    const [profileImage, setProfileImage] = useState<string | null>(null);
    return(
        <>
        <div className={Styles.dProfileContainer}>
            <div className={Styles.dProfileContent}>
                <div className={Styles.dprofile}>
                    <input
                        type="file"
                        accept="image/*"
                        id="profile-upload"
                        style={{ display: 'none' }}
                        onChange={(e) => {
                            const file = e.target.files?.[0];

                            if(file){
                                setProfileImage(URL.createObjectURL(file));
                            }
                        }}
                    />
                    <label htmlFor="profile-upload" className={Styles.changeImg}>
                        Change Image
                    </label>
                    <img src={profileImage || "https://via.placeholder.com/150"} alt='ProfileImg' ></img>
                    <h1>UserName</h1>
                </div>
                <h2>Personal Info:</h2>
                <div className={Styles.dPersonal}>
                    <Input 
                        label='Full Name:'
                        value="Ahmed Mohamed Ali"
                        type='text'
                        />
                    <Input 
                        label='Department:'
                        value="Cardiology"
                        type='text'/>
                    <Input 
                        label='Degree:'
                        value="Expert"
                        type='text'/>
                    <Input 
                        label='Speciality:'
                        value="Heart Diseases"
                        type='text'/>

                </div>
                <div className={Styles.divider}></div>
                <h2>Professional details:</h2>
                <form>
                    <div className={Styles.profDetails}>
                        <Input 
                            label="Emergency Contact:*"
                            value={phone}
                            type="text"
                            onChange={(e) => setPhone(e.target.value)}
                        />
                        <Input 
                            label="Examination Price:*"
                            value={price}
                            type="text"
                            onChange={(e) => setPrice(e.target.value)}
                        />
                    </div>
                    <div className={Styles.dProfileButtons}>
                            <button type="submit" className={Styles.saveButton}>Save Changes</button>
                            <button type="button" className={Styles.cancelButton}>Cancel</button>
                    </div>
                </form>

            </div>
        </div>
        </>
    );
}
export default DoctorProfile;