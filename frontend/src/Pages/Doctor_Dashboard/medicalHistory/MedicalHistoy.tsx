
import MedicationCard from '../../../Components/MedicationCard/MedicationCard';
import Styles from './MedicalHistory.module.css'

import { useLocation, useNavigate } from 'react-router-dom';
import { useEffect, useState } from 'react';

type MedicalRecord = {
    id: number;
    medicationName: string;
    diagnosis: string;
    medication: string;
    treatmentPlan: string;
};

function MedicalHistory(){
    const location = useLocation();
    const navigate = useNavigate();
    const {patientId, patientName} = location.state || {};
    const [records, setRecords] = useState<MedicalRecord[]>([]);
    useEffect(() => {

        async function fetchMedicalHistory(){

            try{
                const response = await fetch(
                    `http://localhost:8080/api/patients/${patientId}/medical-history`,
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
                        <h1>Medical Records for, {patientName}</h1>
                        <button className={Styles.backButton}
                                onClick={() => navigate(-1)}>Back</button>
                    </div>
                    
                    {
                        records.map((record) =>(
                            <MedicationCard 
                                key={record.id}
                                diagnosis={record.diagnosis}
                                medication={record.medication}
                                treatmentPlan={record.treatmentPlan}
                            />
                        ))
                    }
                </div>
            </div>
        </>
    );
}

export default MedicalHistory;