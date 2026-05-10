
import MedicationCard from '../../../../Components/MedicationCard/MedicationCard';
import Styles from './medicalHistory.module.css'
function MedicalHistory(){
    return(
        <>
            <div className={Styles.medHisContainer}>
                <div className={Styles.medHisContent}>
                    <div className={Styles.medHisHead}>
                        <h1>Medical Records for, Patient Name</h1>
                        <button className={Styles.backButton}>Back</button>
                    </div>
                    
                    <MedicationCard />
                </div>
            </div>
        </>
    );
}

export default MedicalHistory;