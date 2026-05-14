
import MedicationCard from '../../../Components/MedicationCard/MedicationCard';
import Styles from './MedicalHistory.module.css'

import { useNavigate, useParams } from 'react-router-dom';
import { useEffect, useState } from 'react';

type MedicalRecord = {
    recordId: number;
    condition: string;
    treatmentPlan: string;
    medications: string[];
    date: string;
};

function MedicalHistory(){
    const navigate = useNavigate();
    const { patientId } = useParams();
    const [records, setRecords] = useState<MedicalRecord[]>([]);
    useEffect(() => {

        async function fetchMedicalHistory(){

            try{
                const response = await fetch(
                    `http://localhost:8080/api/patients/${patientId}/history`,
                    {
                        headers:{
                            'Authorization': `Bearer ${localStorage.getItem('token')}`
                        }
                    }
                );

                if(!response.ok){
                    throw new Error("Failed to fetch medical history");
                }

                const data = await response.json();
                setRecords(data);

            }catch(error){
                console.error(error);
                alert("Could not load medical history");
            }
        }

        if(patientId){
            fetchMedicalHistory();
        }

    }, [patientId]);


    return(
        <>
            <div className={Styles.medHisContainer}>
                <div className={Styles.medHisContent}>
                    <div className={Styles.medHisHead}>
                        <h1>Medical Records</h1>
                        <button className={Styles.backButton}
                                onClick={() => navigate(-1)}>Back</button>
                    </div>
                    
                    {
                        records.map((record) =>(
                            <MedicationCard 
                                key={record.recordId}
                                recordId={record.recordId}
                                diagnosis={record.condition}
                                medications={record.medications}
                                treatmentPlan={record.treatmentPlan}
                                date={record.date}
                            />
                        ))
                    }
                </div>
            </div>
        </>
    );
}

export default MedicalHistory;