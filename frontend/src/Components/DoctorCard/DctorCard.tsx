
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import Doctor from '../Doctor/Doctor';
import Styles from './DoctorCard.module.css'
import { faStar } from '@fortawesome/free-solid-svg-icons';
import Modal from '../Modal/Modal';

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
                <Modal></Modal>
            </div>
        </div>
        </>
    );
}

export default DoctorCard;