
import AppointmentCard from '../../../Components/AppointmentCard/AppointmentCard';
import Styles from './Appointments.module.css'

function Appointments(){
    return(

        <>
        <div className={Styles.AppointmentsConatiner}>
            <div className={Styles.AppointmentsContent}>
                <button className={Styles.bookButton}>Book new appointment</button>
                <div className={Styles.allAppointments}>
                    <h3>All appointments</h3>
                    <AppointmentCard />
                    <AppointmentCard />
                </div>
            </div>
        </div>
        </>

    );
}

export default Appointments;