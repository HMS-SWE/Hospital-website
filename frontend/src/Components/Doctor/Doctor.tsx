
import Styles from './Doctor.module.css'
type doctorProps = {
    name?: string;
    type?: string;
}
function Doctor({name, type}: doctorProps){
    return(
        <>
        <div className={Styles.appDoctor}>
            <div className={Styles.DoctorImg}>

            </div>
            <div className={Styles.DoctorDetails}>
                <h4>{name}</h4>
                <span>{type}</span>
            </div>
        </div>
        </>
    );
}

export default Doctor;