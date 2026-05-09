import { useState, useEffect } from "react";
import Styles from './Diagnose.module.css'

type DiagnoseProps = {
    patientName: string;
}

function Diagnose({patientName}:DiagnoseProps){
    const [diagnosis, setDiagnosis] = useState("");
    const [medications, setMedications] = useState("");
    const [treatmentPlan, setTreatmentPlan] = useState("");
    const [showLeaveModal, setShowLeaveModal] = useState(false);
    const [pendingNavigation, setPendingNavigation] = useState<(() => void) | null>(null);

    const [errors, setErrors] = useState({
        diagnosis: "",
        medications: "",
        treatmentPlan: ""
    });

    const hasUnsavedChanges =
    diagnosis.trim() !== "" ||
    medications.trim() !== "" ||
    treatmentPlan.trim() !== "";

    useEffect(() => {
    const handleBeforeUnload = (e: BeforeUnloadEvent) => {
        if (hasUnsavedChanges) {
            e.preventDefault();
            e.returnValue = "";
        }
    };

    window.addEventListener("beforeunload", handleBeforeUnload);

    return () => {
        window.removeEventListener("beforeunload", handleBeforeUnload);
    };
    }, [hasUnsavedChanges]);

    const validate = () => {
        const newErrors = {
            diagnosis: "",
            medications: "",
            treatmentPlan: ""
        };
        let isValid = true;

        if (!diagnosis.trim()) {
            newErrors.diagnosis = "Diagnosis is required";
            isValid = false;
        }

        if (!medications.trim()) {
            newErrors.medications = "Medications are required";
            isValid = false;
        }

        if (!treatmentPlan.trim()) {
            newErrors.treatmentPlan = "Treatment plan is required";
            isValid = false;
        }

        setErrors(newErrors);
        return isValid;
    }

    const handleSubmit = (e: React.FormEvent) => {
        e.preventDefault();

        if (!validate()) {
            return;
        }
        alert("Diagnosis Saved!");
    };

    const handleLeave = () => {
    if (hasUnsavedChanges) {
        setShowLeaveModal(true);

        setPendingNavigation(() => () => {
            window.history.back();
        });

        return;
    }

    window.history.back();
    };

    return(
        <>
        <div className={Styles.diagnoseContainer}>
            <div className={Styles.diagnoseContents}>
                <div className={Styles.diagnoseHeader}>
                    <h1>Add Diagnosis</h1>
                </div>
                <div className={Styles.diagnoseBody}>
                    <form onSubmit={handleSubmit}>
                        <div className={Styles.diagnoseNameDiagnosis}>
                            <label>Patient Name:</label>
                            <input value={patientName} disabled></input>
                        </div>
                        <div className={Styles.diagnoseNameDiagnosis}>
                            <label htmlFor="diagnosis">Diagnosis:</label>
                            <input  id="diagnosis"
                                    value={diagnosis}
                                    onChange={(e) => setDiagnosis(e.target.value)} />
                            {errors.diagnosis && <span>{errors.diagnosis}</span>}
                        </div><br></br>
                        <div className={Styles.diagnoseMedPlan}>
                            <label htmlFor="medications">Medications:</label>
                            <textarea   id="medications"
                                        value={medications}
                                        onChange={(e) => setMedications(e.target.value)} />
                            {errors.medications && <span>{errors.medications}</span>}
                        </div>
                        <div className={Styles.diagnoseMedPlan}>
                            <label htmlFor="plan">Treatement Plan:</label>
                            <textarea   id="plan"
                                        value={treatmentPlan}
                                        onChange={(e) => setTreatmentPlan(e.target.value)} />
                            {errors.treatmentPlan && <span>{errors.treatmentPlan}</span>}
                        </div>
                        <button type='submit' className={Styles.DiagnosisSubmit}>Save Diagnosis</button>
                        <button type="button" className={Styles.BackButton} onClick={handleLeave}>Back</button>
                    </form> 
                    {showLeaveModal && (
                        <div className={Styles.modalOverlay}>
                            <div className={Styles.modal}>
                                <h2>Unsaved Changes</h2>
                                <p>
                                    You have unsaved diagnosis information.
                                    Are you sure you want to leave?
                                </p>

                                <div className={Styles.modalButtons}>
                                    <button className={Styles.modalLeaveButton}
                                        onClick={() => setShowLeaveModal(false)}
                                    >
                                        Stay
                                    </button>

                                    <button className={Styles.modalLeaveButton}
                                        onClick={() => {
                                            setShowLeaveModal(false);

                                            if (pendingNavigation) {
                                                pendingNavigation();
                                            }
                                        }}
                                    >
                                        Leave
                                    </button>
                                </div>
                            </div>
                        </div>
                    )}
                </div>
            </div>
        </div>
        </>
    );
}
export default Diagnose;