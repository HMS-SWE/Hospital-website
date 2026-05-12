import Doctor from '../Doctor/Doctor';
import Styles from './MedicationCard.module.css'


type MedicationCardProps = {
    diagnosis: string;
    medication: string;
    treatmentPlan: string;
}

function MedicationCard({diagnosis, medication, treatmentPlan}: MedicationCardProps){
    return(
        <>
            <div className={Styles.cardContainer}>
                <div className={Styles.cardContents}>

                    <div className={Styles.medCardUpper}>
                        <Doctor />
                        <div className={Styles.medCardDetails}>
                            <span>09/05/2026</span>
                            <span>Diagnosis: {diagnosis}</span>
                        </div>
                    </div>
                       
                    <div className={Styles.divider}></div>
                    
                    <div className={Styles.medCardLower}>
                        <ul className={Styles.DoctorAppContents}>
                            <li>
                                <input type="checkbox" name='accordion' id={diagnosis}></input>
                                <label htmlFor={diagnosis}>Expand</label>
                                <div className={Styles.medCardExpand}>
                                        <div className={Styles.ExpandCol}>   
                                                <h3>Diagnosis: </h3> 
                                                <span>{diagnosis}</span>
                                        </div>
                                        <div className={Styles.ExpandCol}>   
                                                <h3>Medications:</h3>
                                                    <span>{medication}</span>
                                        </div>
                                        <div className={Styles.ExpandCol}>   
                                                <h3>Treatement Plan:</h3>
                                                <span>{treatmentPlan}</span>
                                        </div>
                                    
                                </div>
                            </li>
                        </ul>

                    </div>
                </div>
            </div>
        </>
    );
}

export default MedicationCard;