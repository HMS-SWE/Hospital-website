
import { useState } from 'react';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import Doctor from '../Doctor/Doctor';
import Styles from './AppointmentCard.module.css'
import { faCalendar, faClock } from '@fortawesome/free-solid-svg-icons';
import { cancelAppointment, editAppointment } from '../api';
import type { AppointmentResponse } from '../api';

type AppointmentCardProps = {
    appointment: AppointmentResponse;
    onUpdated: () => void;
};

function AppointmentCard({ appointment, onUpdated }: AppointmentCardProps){
    const [cancelling, setCancelling] = useState(false);
    const [rescheduling, setRescheduling] = useState(false);
    const [showReschedule, setShowReschedule] = useState(false);

    const [selectedSlotId, setSelectedSlotId] = useState<number | null>(null);
    const [error, setError] = useState('');

    
    const isCancelled = appointment.status === 'CANCELLED';
    const isCompleted = appointment.status === 'COMPLETED';
    const canModify = !isCancelled && !isCompleted;

    const statusClass = isCancelled ? Styles.cancelled
        : appointment.status === 'CONFIRMED' ? Styles.confirmed
        : Styles.pending;

    const handleCancel = async () => {
        const reason = prompt('Please provide a reason for cancellation:');
        if (!reason) return;

        setCancelling(true);
        setError('');
        try {
            await cancelAppointment(appointment.appointmentId, reason);
            onUpdated();
        } catch (err) {
            setError(err instanceof Error ? err.message : 'Cancel failed');
        } finally {
            setCancelling(false);
        }
    };

    const openReschedule = async () => {
        // We need doctorId — but AppointmentResponse doesn't have it.
        // We'll try to get it from the appointment context.
        // For now we prompt for it or use the appointment's schedule info.
        setShowReschedule(true);
        setError('');
    };



    const handleReschedule = async () => {
        if (!selectedSlotId) return;
        setRescheduling(true);
        setError('');
        try {
            await editAppointment(appointment.appointmentId, selectedSlotId);
            setShowReschedule(false);
            onUpdated();
        } catch (err) {
            setError(err instanceof Error ? err.message : 'Reschedule failed');
        } finally {
            setRescheduling(false);
        }
    };

    const formatTime = (time: string) => {
        const [hours, minutes] = time.split(':').map(Number);
        const ampm = hours >= 12 ? 'PM' : 'AM';
        const displayHour = hours % 12 || 12;
        return `${displayHour}:${String(minutes).padStart(2, '0')} ${ampm}`;
    };

    return(
        <>
        <div className={Styles.appCard}>
            <div className={Styles.appCardContents}>
                    <div className={Styles.appHead}>
                        <div className={Styles.appDoctor}>
                            <Doctor name={appointment.doctorName} speciality="" />
                        </div>
                        <div className={Styles.appstatus}>
                            <label className={statusClass}>{appointment.status}</label>
                        </div>
                    </div>
                    <div className={Styles.appDetails}>
                        <div className={Styles.AppDAT}>
                            <div className={Styles.AppDate}>
                                <FontAwesomeIcon icon={faCalendar} />
                                <span>{appointment.date}</span>
                            </div>
                            <div className={Styles.AppTime}>
                                <FontAwesomeIcon icon={faClock} />
                                <span>{formatTime(appointment.startTime)} - {formatTime(appointment.endTime)}</span>
                            </div>
                        </div>
                        {canModify && (
                            <div className={Styles.AppControls}>
                                <button
                                    className={Styles.RescheduleButton}
                                    onClick={openReschedule}
                                    disabled={rescheduling}
                                >
                                    Reschedule
                                </button>
                                <button
                                    className={Styles.cancelButton}
                                    onClick={handleCancel}
                                    disabled={cancelling}
                                >
                                    {cancelling ? 'Cancelling...' : 'Cancel'}
                                </button>
                            </div>
                        )}
                    </div>
                    {appointment.examinationPrice != null && (
                        <div className={Styles.appPrice}>
                            Price: {appointment.examinationPrice} EGP
                        </div>
                    )}
                    {error && <div className={Styles.appError}>{error}</div>}

                    {/* Reschedule Panel */}
                    {showReschedule && (
                        <div className={Styles.reschedulePanel}>
                            <h4>Reschedule Appointment</h4>
                            <p style={{fontSize: '0.85rem', color: '#666', marginBottom: '8px'}}>
                                Enter the new slot ID to reschedule to a different time.
                            </p>
                            <div className={Styles.rescheduleFields}>
                                <label>New Slot ID:</label>
                                <input
                                    type="number"
                                    placeholder="Enter slot ID"
                                    value={selectedSlotId ?? ''}
                                    onChange={(e) => setSelectedSlotId(Number(e.target.value) || null)}
                                />
                            </div>
                            <div className={Styles.rescheduleActions}>
                                <button
                                    className={Styles.RescheduleButton}
                                    onClick={handleReschedule}
                                    disabled={!selectedSlotId || rescheduling}
                                >
                                    {rescheduling ? 'Rescheduling...' : 'Confirm'}
                                </button>
                                <button
                                    className={Styles.cancelButton}
                                    onClick={() => setShowReschedule(false)}
                                >
                                    Close
                                </button>
                            </div>
                        </div>
                    )}
            </div>
        </div>
        </>
    );
}

export default AppointmentCard;