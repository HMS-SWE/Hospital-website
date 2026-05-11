
import DoctorApp from '../../../Components/Doctor_DoctorApp/DoctorApp';
import Styles from './Schedule.module.css'
import { useState, useEffect } from 'react';
import { mapAppointmentStatus } from './StatusMapper';

type DoctorAppointmentDTO = {
    appointmentId: number;
    patientFullName: string;
    startTime: string;
    endTime: string;
    status: string;
};

function Schedule(){
    const user = JSON.parse(localStorage.getItem("user") || "{}");
    const doctorName = user.name || "Doctor";
    const today = new Date();
    const formattedDate = today.toLocaleDateString('en-GB');
    const [showModal, setShowModal] = useState(false);
    const [appointments, setAppointments] = useState<any[]>([])
    const hasPendingAppointments = appointments.some(
        (a) => a.status === "Pending"
        );
    async function handleCancelAll(){
            const userString = localStorage.getItem('user');
            if (!userString) {
                alert("Session expired. Please log in again.");
                return;
            }
            const user = JSON.parse(userString);
            const doctorId = user.id;

            try{
                const response = await fetch(`http://localhost:8080/api/schedules/doctor/${doctorId}/schedule/cancel`,{
                    method: 'PATCH',
                    headers:{
                        'Content-Type': 'application/json',
                        'Authorization': `Bearer ${localStorage.getItem('token')}`
                    }
                });
                if (response.ok){
                    const data = await response.json();
                    setAppointments((prevAppointments) =>
                    prevAppointments.map((appointment) => {
                        if (appointment.status === "Pending"){
                            return{
                                ...appointment,
                                status: "Cancelled"
                            };
                        } 
                        return appointment;
                    })
                    )
                    alert(`${data.cancelledCount || 'All'} Appointments has been cancelled successfully!`)
                }else if(response.status === 403){
                    alert("Forbidden: You do not have permission to cancel this schedule.");
                }else{
                    alert("Failed to cancel schedule. Please try again.");
                }
            }
            catch(error){
                console.error("Network error: ", error);
                alert("Could Not Connect to Server");
            }finally{
                setShowModal(false);
            }
        }
    useEffect(() => {
        async function fetchSchedule() {
            try {
                const res = await fetch(
                    "http://localhost:8080/api/appointments/doctor/today",
                    {
                        headers: {
                            "Authorization": `Bearer ${localStorage.getItem("token")}`
                        }
                    }
                );

                if (!res.ok) throw new Error("Failed to fetch");

                const data = await res.json();
                console.log("RAW API RESPONSE:", data);
                const formatted = data.map((a: DoctorAppointmentDTO) => ({
                    id: a.appointmentId,
                    patientName: a.patientFullName,
                    time: `${a.startTime} - ${a.endTime}`,
                    status: mapAppointmentStatus(a.status)
                }));

                setAppointments(formatted);

            } catch (err) {
                console.error(err);
                alert("Failed to load schedule");
            }
        }

        fetchSchedule();
    }, []);
    return(
        <>
        <div className={Styles.ScheduleContainer}>
            <div className={Styles.ScheduleContents}>
                    <div className={Styles.ScheduleHeadline}>
                        <h1>Welcome, {doctorName}</h1>
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