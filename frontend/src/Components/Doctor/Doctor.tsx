
import Styles from './Doctor.module.css'

type DoctorProps = {
    name?: string;
    specialty?: string;
};

function Doctor({ name = 'Doctor Name', specialty = 'Specialization' }: DoctorProps){
    return(
        <>
        <div className={Styles.appDoctor}>
            <div className={Styles.DoctorImg}>

            </div>
            <div className={Styles.DoctorDetails}>
                <h4>{name}</h4>
                <span>{specialty}</span>
            </div>
        </div>
        </>
    );
}

export default Doctor;