
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import Doctor from '../Doctor/Doctor';
import Styles from './AppointmentCard.module.css'
import { faCalendar, faClock } from '@fortawesome/free-solid-svg-icons';


function AppointmentCard(){
    return(
        <>
        <div className={Styles.appCard}>
            <div className={Styles.appCardContents}>
                    <div className={Styles.appHead}>
                        <div className={Styles.appDoctor}>
                            <Doctor />
                        </div>
                        <div className={Styles.appstatus}>
                            <label>Confirmed</label>
                        </div>
                    </div>
                    <div className={Styles.appDetails}>
                        <div className={Styles.AppDAT}>
                            <div className={Styles.AppDate}>
                                <FontAwesomeIcon icon={faCalendar} />
                                <span>28-04-2026</span>
                            </div>
                            <div className={Styles.AppTime}>
                                <FontAwesomeIcon icon={faClock} />
                                <span>10:00 AM</span>
                            </div>
                        </div>
                        <div className={Styles.AppControls}>
                                <button className={Styles.RescheduleButton}>Reschedule</button>
                                <button className={Styles.cancelButton}>Cancel</button>
                        </div>
                    </div>
            </div>
        </div>
        </>
    );
}

export default AppointmentCard;