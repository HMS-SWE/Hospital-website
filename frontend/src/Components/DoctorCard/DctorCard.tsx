
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import Doctor from '../Doctor/Doctor';
import Styles from './DoctorCard.module.css'
import { faStar } from '@fortawesome/free-solid-svg-icons';
import Modal from '../Modal/Modal';
import type { DoctorSearchResult } from '../api';

type DoctorCardProps = {
    doctor: DoctorSearchResult;
};

function DoctorCard({ doctor }: DoctorCardProps){
    return(
        <>
        <div className={Styles.CardBody}>
            <div className={Styles.Upper}>
                <Doctor name={doctor.fullName} specialty={doctor.specialtyName} />
                <span>4.8<FontAwesomeIcon icon={faStar}></FontAwesomeIcon></span>
            </div>
            <div className={Styles.DCdivider}></div>
            <div className={Styles.Lower}>
                <h2>{doctor.examinationPrice != null ? `${doctor.examinationPrice} EGP` : 'N/A'}</h2>
                <Modal doctorId={doctor.id} doctorName={doctor.fullName}></Modal>
            </div>
        </div>
        </>
    );
}

export default DoctorCard;