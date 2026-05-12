
import { useNavigate } from 'react-router-dom';
import Doctor from '../Doctor/Doctor';
import Styles from './DoctorApp.module.css'


type DoctorAppProps = {
    id: string;
    patientName: string;
    time: string;
    status: string;
    type: string;
}

function DoctorApp({ id, patientName, time, status, type }: DoctorAppProps){
    const navigate = useNavigate();
    function handleViewMedicalHistory(){
        navigate('/doctor/medical-history', {
            state: {
                patientId: id,
                patientName: patientName
            }
        });
    }
    return(
        <>
        <div className={Styles.DoctorAppContainer}>
                <div className={Styles.DocAppDet}>
                    <Doctor name={patientName} type={type}/>
                    <div className={Styles.AppDAT}>
                        <div className={Styles.Time}>
                        <span>Time</span>
                        <span>{time}</span>
                        </div>
                        <div className={`
                                    ${Styles.Status}
                                    ${status === 'Completed' ? Styles.Completed : ''}
                                    ${status === 'Pending' ? Styles.Pending : ''}
                                    ${status === 'Cancelled' ? Styles.Cancelled : ''}
                                `}>
                            <span>{status}</span>
                        </div>
                    </div>
                    
                </div>
                <div className={Styles.divider}></div>
                <ul className={Styles.DoctorAppContents}>
                    <li>
                        <input type="checkbox" name='accordion' id={id}></input>
                        <label htmlFor={id}>Examine</label>
                        <div className={Styles.content}>
                            <button className={Styles.AppViewButton} onClick={handleViewMedicalHistory}>View Medical History</button>
                            <button className={Styles.AppUploadButton}>Upload Diagnosis</button>
                            <button className={Styles.AppDShowButton}>Didn't Show up</button>
                        </div>
                    </li>
                </ul>
        </div>

        </>
    );
    
}

export default DoctorApp;