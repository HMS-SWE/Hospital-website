
import Styles from './Doctor.module.css'

function Doctor(){
    return(
        <>
        <div className={Styles.appDoctor}>
            <div className={Styles.DoctorImg}>

            </div>
            <div className={Styles.DoctorDetails}>
                <h4>Doctor Name</h4>
                <span>Specialization</span>
            </div>
        </div>
        </>
    );
}

export default Doctor;