
import { useState, useEffect, useRef } from 'react';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import Styles from './BookAppointment.module.css'
import { faSearch } from '@fortawesome/free-solid-svg-icons';
import DoctorCard from '../../../Components/DoctorCard/DctorCard';
import { searchDoctors } from '../../../Components/api';
import type { DoctorSearchResult } from '../../../Components/api';


function BookAppointment(){
    const [query, setQuery] = useState('');
    const [doctors, setDoctors] = useState<DoctorSearchResult[]>([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');
    const debounceRef = useRef<ReturnType<typeof setTimeout> | null>(null);

    const fetchDoctors = (searchQuery: string) => {
        setLoading(true);
        setError('');
        searchDoctors(searchQuery)
            .then((data) => {
                setDoctors(data);
            })
            .catch((err) => {
                setError(err instanceof Error ? err.message : 'Failed to search doctors');
            })
            .finally(() => setLoading(false));
    };

    useEffect(() => {
        fetchDoctors('');
    }, []);

    const handleSearchChange = (value: string) => {
        setQuery(value);
        if (debounceRef.current) clearTimeout(debounceRef.current);
        debounceRef.current = setTimeout(() => {
            fetchDoctors(value);
        }, 400);
    };

    const handleSearchSubmit = (e: React.FormEvent) => {
        e.preventDefault();
        if (debounceRef.current) clearTimeout(debounceRef.current);
        fetchDoctors(query);
    };

    return(
        <>
        <div className={Styles.BA}>
            <div className={Styles.BAContents}>
                <h1> Book Appointment</h1>
                <form onSubmit={handleSearchSubmit}>
                    <div className={Styles.BASearch}>
                        <button type="submit"><FontAwesomeIcon icon={faSearch}></FontAwesomeIcon></button>
                        <input
                            placeholder="      Search by doctor name or speciality"
                            value={query}
                            onChange={(e) => handleSearchChange(e.target.value)}
                        ></input>
                    </div>
                </form>

                {loading && <p style={{padding: '16px'}}>Loading doctors...</p>}
                {error && <p style={{padding: '16px', color: '#c00'}}>{error}</p>}

                <div className={Styles.BAboard}>
                    {!loading && doctors.length === 0 && !error && (
                        <p style={{padding: '16px', color: '#888'}}>No doctors found.</p>
                    )}
                    {doctors.map((doctor) => (
                        <DoctorCard key={doctor.id} doctor={doctor} />
                    ))}
                </div>
            </div>
        </div>
        </>
    );
}

export default BookAppointment;