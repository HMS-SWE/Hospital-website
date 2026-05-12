import Doctor from '../Doctor/Doctor';
import Styles from './MedicationCard.module.css'

function MedicationCard(){
    return(
        <>
            <div className={Styles.cardContainer}>
                <div className={Styles.cardContents}>

                    <div className={Styles.medCardUpper}>
                        <Doctor />
                        <div className={Styles.medCardDetails}>
                            <span>09/05/2026</span>
                            <span>Diagnosis: Severe Trauma</span>
                        </div>
                    </div>
                       
                    <div className={Styles.divider}></div>
                    
                    <div className={Styles.medCardLower}>
                        <ul className={Styles.DoctorAppContents}>
                            <li>
                                <input type="checkbox" name='accordion' id='1'></input>
                                <label htmlFor='1'>Expand</label>
                                <div className={Styles.medCardExpand}>
                                        <div className={Styles.ExpandCol}>   
                                                <h3>Diagnosis: </h3> 
                                                <span>Bro is cooked Fr Fr</span>
                                        </div>
                                        <div className={Styles.ExpandCol}>   
                                                <h3>Medications:</h3>
                                                    <span>Doliprane 1000mg</span>
                                                    <span> Panadol Extra </span>
                                        </div>
                                        <div className={Styles.ExpandCol}>   
                                                <h3>Treatement Plan:</h3>
                                                <span>X1 before lunch</span>
                                                <span>X2 daily</span>
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