
import DoctorApp from '../../../Components/Doctor_DoctorApp/DoctorApp';
import Styles from './Schedule.module.css'
import { mockAppointments } from './mockAppointments';
function Schedule(){
    const today = new Date();
    const formattedDate = today.toLocaleDateString('en-GB');

    return(
        <>
        <div className={Styles.ScheduleContainer}>
            <div className={Styles.ScheduleContents}>
                    <div className={Styles.ScheduleHeadline}>
                        <h1>Welcome, Dr.Name</h1>
                        <div>
                            <h2>Scedule For: {formattedDate}</h2>
                            <span>{mockAppointments.length} Appointments</span>
                            {mockAppointments.length > 0?(    
                                <button>Cancel All</button>
                            ):(
                                <div></div>
                            )}
                        </div>
                        
                    </div>
                    <div className={Styles.TodaySchedule}>
                        {mockAppointments.length > 0? (
                            mockAppointments.map((appointment) => (
                            <DoctorApp
                            key={appointment.id}
                            id={appointment.id}
                            patientName={appointment.patientName}
                            time={appointment.time}
                            status={appointment.status}
                            type={appointment.type}
                            />))
                        ):(
                            <h1>No Appointments Till Now!</h1>
                        )
                            
                        }
                        
                    </div>
            </div>
        </div>
        </>
    );
}

export default Schedule;