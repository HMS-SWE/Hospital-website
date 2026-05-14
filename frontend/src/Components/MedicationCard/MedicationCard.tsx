import Doctor from '../Doctor/Doctor';
import Styles from './MedicationCard.module.css'


type MedicationCardProps = {
    recordId: number;
    diagnosis: string;
    medications: string[];
    treatmentPlan: string;
    date: string;
}

function MedicationCard({recordId, diagnosis, medications, treatmentPlan, date}: MedicationCardProps){
    const safeId = `expand-${recordId}`;
    return(
        <>
            <div className={Styles.cardContainer}>
                <div className={Styles.cardContents}>

                    <div className={Styles.medCardUpper}>
                        <Doctor />
                        <div className={Styles.medCardDetails}>
                            <span>{new Date(date).toLocaleDateString()}</span>
                            <span>Diagnosis: {diagnosis}</span>
                        </div>
                    </div>
                       
                    <div className={Styles.divider}></div>
                    
                    <div className={Styles.medCardLower}>
                        <ul className={Styles.DoctorAppContents}>
                            <li>
                                <input type="checkbox" id={safeId} aria-controls={`panel-${recordId}`}></input>
                                <label htmlFor={safeId}>Expand</label>
                                <div className={Styles.medCardExpand}>
                                        <div className={Styles.ExpandCol}>   
                                                <h3>Diagnosis: </h3> 
                                                <span>{diagnosis}</span>
                                        </div>
                                        <div className={Styles.ExpandCol}>   
                                                <h3>Medications:</h3>
                                                    <ul>
                                                        {medications.map((m, i) => (
                                                            <li key={i}>{m}</li>
                                                        ))}
                                                    </ul>
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