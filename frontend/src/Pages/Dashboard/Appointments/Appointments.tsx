
import { useNavigate } from 'react-router-dom';
import AppointmentCard from '../../../Components/AppointmentCard/AppointmentCard';
import Styles from './Appointments.module.css'

function Appointments(){
    const navigate = useNavigate();
    return(

        <>
        <div className={Styles.AppointmentsConatiner}>
            <div className={Styles.AppointmentsContent}>
                <h1>My appointments</h1>
                <button className={Styles.bookButton}
                        onClick={() => navigate("/dashboard/book-appointment")}
                        >Book new appointment</button>
                <div className={Styles.allAppointments}>
                    <AppointmentCard />
                    <AppointmentCard />
                </div>
            </div>
        </div>
        </>

    );
}

export default Appointments;