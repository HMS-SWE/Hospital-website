
import DoctorApp from '../../../Components/Doctor_DoctorApp/DoctorApp';
import Styles from './Schedule.module.css'
import { mockAppointments } from './mockAppointments';
import { useState } from 'react';

function Schedule(){
    const today = new Date();
    const formattedDate = today.toLocaleDateString('en-GB');
    const [showModal, setShowModal] = useState(false);
    const [appointments, setAppointments] = useState(mockAppointments);
    const hasPendingAppointments = appointments.some(
        (a) => a.status === "Pending"
        );
    function handleCancelAll(){

            const updatedAppointments = appointments.map((appointment) => {

                if(appointment.status === "Pending"){
                    return {
                        ...appointment,
                        status: "Cancelled" as const
                    };
                }

                return appointment;
            });

            setAppointments(updatedAppointments);
            alert("All appointments cancelled");
            

            setShowModal(false);
        }
    return(
        <>
        <div className={Styles.ScheduleContainer}>
            <div className={Styles.ScheduleContents}>
                    <div className={Styles.ScheduleHeadline}>
                        <h1>Welcome, Dr.Name</h1>
                        <div>
                            <h2>Schedule For: {formattedDate}</h2>
                            <span>{appointments.length} Appointments</span>
                            {hasPendingAppointments && (
                            <button onClick={() => setShowModal(true)}>
                                Cancel All
                            </button>
                        )}
                        </div>
                        
                    </div>
                    <div className={Styles.TodaySchedule}>
                        {appointments.length > 0? (
                            appointments.map((appointment) => (
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

        {
            showModal && (
                <div className={Styles.ModalOverlay}>
                    <div className={Styles.Modal}>
                        <h2>Cancel All Appointments?</h2>
                        <p>
                            This action cannot be undone.
                        </p>

                        <div className={Styles.ModalButtons}>
                            <button
                                onClick={handleCancelAll}
                                className={Styles.ModalYesDelete}
                            >
                                Yes, Cancel
                            </button>

                            <button
                                onClick={() => setShowModal(false)}
                            >
                                Close
                            </button>
                        </div>
                    </div>
                </div>
            )
        }
        </>
    );
}

export default Schedule;