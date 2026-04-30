
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import Doctor from '../Doctor/Doctor';
import Styles from './DoctorCard.module.css'
import { faStar } from '@fortawesome/free-solid-svg-icons';

function DoctorCard(){
    return(
        <>
        <div className={Styles.CardBody}>
            <div className={Styles.Upper}>
                <Doctor />
                <span>4.8<FontAwesomeIcon icon={faStar}></FontAwesomeIcon></span>
            </div>
            <div className={Styles.DCdivider}></div>
            <div className={Styles.Lower}>
                <h2>100 EGP</h2>
                <button className={Styles.bookNowButton}>Book Now</button>
            </div>
        </div>
        </>
    );
}

export default DoctorCard;