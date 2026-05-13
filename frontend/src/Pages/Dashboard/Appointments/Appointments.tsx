
import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import AppointmentCard from '../../../Components/AppointmentCard/AppointmentCard';
import Styles from './Appointments.module.css'
import { getMyAppointments } from '../../../Components/api';
import type { AppointmentResponse } from '../../../Components/api';

function Appointments(){
    const navigate = useNavigate();
    const [appointments, setAppointments] = useState<AppointmentResponse[]>([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');

    const fetchAppointments = () => {
        setLoading(true);
        setError('');
        getMyAppointments()
            .then((data) => {
                setAppointments(data);
            })
            .catch((err) => {
                setError(err instanceof Error ? err.message : 'Failed to load appointments');
            })
            .finally(() => setLoading(false));
    };

    useEffect(() => {
        let cancelled = false;
        getMyAppointments()
            .then((data) => {
                if (!cancelled) setAppointments(data);
            })
            .catch((err) => {
                if (!cancelled) setError(err instanceof Error ? err.message : 'Failed to load appointments');
            })
            .finally(() => {
                if (!cancelled) setLoading(false);
            });
        return () => { cancelled = true; };
    }, []);

    return(

        <>
        <div className={Styles.AppointmentsConatiner}>
            <div className={Styles.AppointmentsContent}>
                <h1>My appointments</h1>
                <button className={Styles.bookButton}
                        onClick={() => navigate("/dashboard/book-appointment")}
                        >Book new appointment</button>
                
                {loading && <p style={{padding: '16px'}}>Loading appointments...</p>}
                {error && <p style={{padding: '16px', color: '#c00'}}>{error}</p>}
                
                <div className={Styles.allAppointments}>
                    {!loading && appointments.length === 0 && !error && (
                        <p style={{padding: '16px', color: '#888'}}>No appointments yet.</p>
                    )}
                    {appointments.map((appointment) => (
                        <AppointmentCard
                            key={appointment.appointmentId}
                            appointment={appointment}
                            onUpdated={fetchAppointments}
                        />
                    ))}
                </div>
            </div>
        </div>
        </>

    );
}

export default Appointments;