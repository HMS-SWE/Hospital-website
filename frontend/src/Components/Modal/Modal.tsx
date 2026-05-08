import { useState, type FormEvent } from 'react';
import Styles from './Modal.module.css';
import Doctor from '../Doctor/Doctor';

function Modal() {
    const [modal, setModal] = useState(false);
    const [step, setStep] = useState(1);
    
    // State to hold form data between Step 1 and Step 2
    const [appointmentData, setAppointmentData] = useState<{day: string, time: string} | null>(null);

    const toggleModal = () => {
        setModal(!modal);
        setStep(1); 
        setAppointmentData(null);
    };

    // Step 1: Just validates and moves to the "Are you sure?" screen
    const handleNextStep = (e: FormEvent<HTMLFormElement>) => {
        e.preventDefault();
        const formData = new FormData(e.currentTarget);
        
        setAppointmentData({
            day: formData.get('appointmentDay') as string,
            time: formData.get('appointmentTime') as string,
        });

        setStep(2);
    };

    // Step 2: This is where the actual "Submission" happens
    const handleFinalConfirm = () => {
        console.log("Final Submission to Backend:", appointmentData);
        
        // Add your fetch/axios logic here
        
        alert("Appointment Confirmed!");
        toggleModal(); // Close and return to main screen
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
                            
                            {/* STEP 1: DATA COLLECTION */}
                            {step === 1 && (
                                <>
                                    <h2>Appointment Booking</h2>
                                    <Doctor name='Doctor' type='test'/>

                                    <form onSubmit={handleNextStep}>
                                        <div className={Styles.bookingFields}>
                                            <div className={Styles.bookingField}>
                                                <label htmlFor="day">Choose Appointment Day:*</label>
                                                <select name="appointmentDay" id="day" className={Styles.fieldSelect} required>
                                                    <option value="Saturday">Saturday</option>
                                                    <option value="Sunday">Sunday</option>
                                                    <option value="Monday">Monday</option>
                                                </select>
                                            </div>

                                            <div className={Styles.bookingField}>
                                                <label htmlFor="time">Choose Appointment Time:*</label>
                                                <select name="appointmentTime" id="time" className={Styles.fieldSelect} required>
                                                    <option value="10:00AM">10:00 AM - 10:15 AM</option>
                                                    <option value="02:15PM" disabled>2:15 PM - 2:30 PM</option>
                                                    <option value="02:30PM">2:30 PM - 2:45 PM</option>
                                                </select>
                                            </div>
                                        </div>

                                        <div className={Styles.buttonGroup}>
                                            <button type="submit" className={Styles.submitModal}>
                                                Book appointment
                                            </button>
                                        </div>
                                    </form>
                                </>
                            )}

                            {/* STEP 2: "ARE YOU SURE?" CONFIRMATION */}
                            {step === 2 && (
                                <div className={Styles.confirmationView}>
                                    <h2>Are you sure?</h2>
                                    <p>
                                        Please confirm your appointment for 
                                        <strong> {appointmentData?.day}</strong> at 
                                        <strong> {appointmentData?.time}</strong>.
                                    </p>
                                    
                                    <div className={Styles.buttonGroup}>
                                        <button 
                                            className={Styles.submitModal} 
                                            onClick={handleFinalConfirm}
                                        >
                                            Confirm
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