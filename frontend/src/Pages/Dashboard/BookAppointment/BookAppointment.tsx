
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import Styles from './BookAppointment.module.css'
import { faSearch } from '@fortawesome/free-solid-svg-icons';
import DoctorCard from '../../../Components/DoctorCard/DctorCard';


function BookAppointment(){
    return(
        <>
        <div className={Styles.BA}>
            <div className={Styles.BAContents}>
                <h1> Book Appointment</h1>
                <form>
                    <div className={Styles.BASearch}>
                        <button><FontAwesomeIcon icon={faSearch}></FontAwesomeIcon></button>
                        <input placeholder="      Search by doctor name or speciality" ></input>
                    </div>
                </form>
                <div className={Styles.BAboard}>
                    <DoctorCard />
                    <DoctorCard />
                </div>
            </div>
        </div>
        </>
    );
}

export default BookAppointment;