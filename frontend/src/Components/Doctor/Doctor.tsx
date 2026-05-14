
import Styles from './Doctor.module.css'
type doctorProps = {
    name?: string;
    speciality?: string;
}
function Doctor({name, speciality}: doctorProps){
    return(
        <>
        <div className={Styles.appDoctor}>
            <div className={Styles.DoctorImg}>

            </div>
            <div className={Styles.DoctorDetails}>
                <h4>{name}</h4>
                <span>{speciality}</span>
            </div>
        </div>
        </>
    );
}

export default Doctor;