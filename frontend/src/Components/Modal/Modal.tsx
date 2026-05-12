import { useState, useEffect, type FormEvent } from 'react';
import Styles from './Modal.module.css';
import Doctor from '../Doctor/Doctor';
import { getAvailableDays, getAvailableSlots, bookAppointment } from '../api';
import type { DayResponse, SlotResponse } from '../api';

type ModalProps = {
    doctorId: number;
    doctorName: string;
};

function Modal({ doctorId, doctorName }: ModalProps) {
    const [modal, setModal] = useState(false);
    const [step, setStep] = useState(1);
    
    const [days, setDays] = useState<DayResponse[]>([]);
    const [slots, setSlots] = useState<SlotResponse[]>([]);
    const [loadingDays, setLoadingDays] = useState(false);
    const [loadingSlots, setLoadingSlots] = useState(false);

    const [selectedDate, setSelectedDate] = useState('');
    const [selectedSlotId, setSelectedSlotId] = useState<number | null>(null);
    const [selectedSlotLabel, setSelectedSlotLabel] = useState('');

    const [booking, setBooking] = useState(false);
    const [error, setError] = useState('');

    const toggleModal = () => {
        setModal(!modal);
        setStep(1); 
        setDays([]);
        setSlots([]);
        setSelectedDate('');
        setSelectedSlotId(null);
        setSelectedSlotLabel('');
        setError('');
    };

    useEffect(() => {
        if (!modal) return;
        setLoadingDays(true);
        setError('');
        getAvailableDays(doctorId)
            .then((data) => {
                setDays(data);
                if (data.length > 0) {
                    setSelectedDate(data[0].date);
                }
            })
            .catch((err) => {
                setError(err instanceof Error ? err.message : 'Failed to load available days');
            })
            .finally(() => setLoadingDays(false));
    }, [modal, doctorId]);

    useEffect(() => {
        if (!selectedDate || !modal) return;
        setLoadingSlots(true);
        setSlots([]);
        setSelectedSlotId(null);
        setSelectedSlotLabel('');
        getAvailableSlots(doctorId, selectedDate)
            .then((data) => {
                setSlots(data);
            })
            .catch((err) => {
                setError(err instanceof Error ? err.message : 'Failed to load available slots');
            })
            .finally(() => setLoadingSlots(false));
    }, [selectedDate, doctorId, modal]);

    const formatTime = (time: string) => {
        const [hours, minutes] = time.split(':').map(Number);
        const ampm = hours >= 12 ? 'PM' : 'AM';
        const displayHour = hours % 12 || 12;
        return `${displayHour}:${String(minutes).padStart(2, '0')} ${ampm}`;
    };

    const handleNextStep = (e: FormEvent<HTMLFormElement>) => {
        e.preventDefault();
        if (!selectedSlotId) {
            setError('Please select a time slot');
            return;
        }
        setError('');
        setStep(2);
    };

    const handleFinalConfirm = async () => {
        if (!selectedSlotId) return;
        setBooking(true);
        setError('');
        try {
            await bookAppointment(selectedSlotId);
            alert("Appointment Confirmed!");
            toggleModal();
        } catch (err) {
            setError(err instanceof Error ? err.message : 'Booking failed');
            setStep(1);
        } finally {
            setBooking(false);
        }
    };

    return (
        <>
            <button onClick={toggleModal} className={Styles.ModalButton}>
                Book Now
            </button>

            {modal && (
                <div className={Styles.modal}>
                    <div className={Styles.overlay} onClick={toggleModal}>
                        <div className={Styles.modalContent} onClick={(e) => e.stopPropagation()}>
                            
                            {step === 1 && (
                                <>
                                    <h2>Appointment Booking</h2>
                                    <Doctor name={doctorName} />

                                    {error && <p style={{color: '#c00', fontSize: '0.85rem'}}>{error}</p>}

                                    <form onSubmit={handleNextStep}>
                                        <div className={Styles.bookingFields}>
                                            <div className={Styles.bookingField}>
                                                <label htmlFor="day">Choose Appointment Day:*</label>
                                                {loadingDays ? (
                                                    <p>Loading available days...</p>
                                                ) : days.length === 0 ? (
                                                    <p>No available days found</p>
                                                ) : (
                                                    <select
                                                        name="appointmentDay"
                                                        id="day"
                                                        className={Styles.fieldSelect}
                                                        required
                                                        value={selectedDate}
                                                        onChange={(e) => setSelectedDate(e.target.value)}
                                                    >
                                                        {days.map((day) => (
                                                            <option key={day.date} value={day.date}>
                                                                {day.dayName} — {day.date}
                                                            </option>
                                                        ))}
                                                    </select>
                                                )}
                                            </div>

                                            <div className={Styles.bookingField}>
                                                <label htmlFor="time">Choose Appointment Time:*</label>
                                                {loadingSlots ? (
                                                    <p>Loading available slots...</p>
                                                ) : slots.length === 0 && selectedDate ? (
                                                    <p>No available slots for this day</p>
                                                ) : (
                                                    <select
                                                        name="appointmentTime"
                                                        id="time"
                                                        className={Styles.fieldSelect}
                                                        required
                                                        value={selectedSlotId ?? ''}
                                                        onChange={(e) => {
                                                            const slotId = Number(e.target.value);
                                                            setSelectedSlotId(slotId);
                                                            const slot = slots.find(s => s.slotId === slotId);
                                                            if (slot) {
                                                                setSelectedSlotLabel(`${formatTime(slot.startTime)} - ${formatTime(slot.endTime)}`);
                                                            }
                                                        }}
                                                    >
                                                        <option value="">Select a time slot</option>
                                                        {slots.map((slot) => (
                                                            <option key={slot.slotId} value={slot.slotId}>
                                                                {formatTime(slot.startTime)} - {formatTime(slot.endTime)}
                                                            </option>
                                                        ))}
                                                    </select>
                                                )}
                                            </div>
                                        </div>

                                        <div className={Styles.buttonGroup}>
                                            <button
                                                type="submit"
                                                className={Styles.submitModal}
                                                disabled={!selectedSlotId || loadingDays || loadingSlots}
                                            >
                                                Book appointment
                                            </button>
                                        </div>
                                    </form>
                                </>
                            )}

                            {step === 2 && (
                                <div className={Styles.confirmationView}>
                                    <h2>Are you sure?</h2>
                                    <p>
                                        Please confirm your appointment with
                                        <strong> {doctorName}</strong> on 
                                        <strong> {selectedDate}</strong> at 
                                        <strong> {selectedSlotLabel}</strong>.
                                    </p>
                                    
                                    {error && <p style={{color: '#c00', fontSize: '0.85rem'}}>{error}</p>}

                                    <div className={Styles.buttonGroup}>
                                        <button 
                                            className={Styles.submitModal} 
                                            onClick={handleFinalConfirm}
                                            disabled={booking}
                                        >
                                            {booking ? 'Booking...' : 'Confirm'}
                                        </button>
                                        <button 
                                            className={Styles.closeModal} 
                                            onClick={toggleModal}
                                        >
                                            Cancel
                                        </button>
                                    </div>
                                </div>
                            )}

                        </div>
                    </div>
                </div>
            )}
        </>
    );
}

export default Modal;