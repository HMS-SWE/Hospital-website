import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faCalendar, faFileMedical, faPills, faClock } from '@fortawesome/free-solid-svg-icons';
import { Link } from 'react-router-dom';
import Styles from './Dashboard.module.css';
import { useEffect, useState } from 'react';
import { apiCall, getCurrentUser } from '../../../utils/api';

interface UserProfile {
  fullName: string;
  email: string;
}

const appointments = [
  {
    doctor: 'Dr. Sarah Johnson',
    specialty: 'Cardiology',
    date: '2026-04-28',
    time: '10:00 AM',
    status: 'confirmed',
  },
  {
    doctor: 'Dr. Michael Chen',
    specialty: 'General Medicine',
    date: '2026-05-05',
    time: '2:30 PM',
    status: 'pending',
  },
];


const medications = [
  {
    name: 'Lisinopril 10mg',
    frequency: 'Once daily',
    since: '2026-03-01',
  },
  {
    name: 'Vitamin D',
    frequency: 'Once daily',
    since: '2026-02-15',
  },
];

function Dashboard() {
  const [name, setName] = useState("User");
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchProfile = async () => {
      const user = getCurrentUser();
      if (!user) {
        setLoading(false);
        return;
      }

      try {
        const endpoint = user.role === 'PATIENT' 
          ? `/patients/${user.userId}/profile`
          : `/doctors/${user.userId}/profile`;
        
        const response = await apiCall(endpoint);
        if (response.ok) {
          const profile: UserProfile = await response.json();
          setName(profile.fullName || profile.email);
        } else {
          // Fallback to stored user info
          const storedUser = JSON.parse(localStorage.getItem("user") || "{}");
          setName(storedUser.email || "User");
        }
      } catch (error) {
        console.error("Failed to fetch profile:", error);
        // Fallback to stored user info
        const storedUser = JSON.parse(localStorage.getItem("user") || "{}");
        setName(storedUser.email || "User");
      } finally {
        setLoading(false);
      }
    };

    fetchProfile();
  }, []);

  if (loading) {
    return <div>Loading...</div>;
  }
  return (
    <div className={Styles.dashboardPage}>
      <header className={Styles.pageHeader}>
        <div>
          <p className={Styles.overline}>Dashboard</p>
          <h1 className={Styles.pageTitle}>Welcome back, // user name // </h1>
          <p className={Styles.pageSubtitle}>Here is a quick summary of your patient portal activity.</p>
        </div>
        <div className={Styles.headerBadge}>
          <span className={Styles.statusPill}>Active</span>
        </div>
      </header>

      <section className={Styles.statsGrid}>
        <article className={Styles.statCard}>
          <div className={Styles.statHeader}>
            <span className={Styles.statIcon}>
              <FontAwesomeIcon icon={faCalendar} />
            </span>
            <span className={Styles.statLabel}>Upcoming Appointments</span>
          </div>
          <div className={Styles.statValue}>2</div>
          <div className={Styles.statMeta}>Next visit: 28 Apr 2026</div>
        </article>

        <article className={Styles.statCard}>
          <div className={Styles.statHeader}>
            <span className={Styles.statIcon}>
              <FontAwesomeIcon icon={faPills} />
            </span>
            <span className={Styles.statLabel}>Active Medications</span>
          </div>
          <div className={Styles.statValue}>2</div>
          <div className={Styles.statMeta}>Last updated today</div>
        </article>

        <article className={Styles.statCard}>
          <div className={Styles.statHeader}>
            <span className={Styles.statIcon}>
              <FontAwesomeIcon icon={faFileMedical} />
            </span>
            <span className={Styles.statLabel}>Medical Records</span>
          </div>
          <div className={Styles.statValue}>8</div>
          <div className={Styles.statMeta}>All records are current</div>
        </article>
      </section>

      <section className={Styles.section}>
        <div className={Styles.sectionHeader}>
          <div>
            <h2>Upcoming Appointments</h2>
            <p className={Styles.sectionSubtitle}>Your next appointments at a glance.</p>
          </div>
          <Link className={Styles.viewAllLink} to="/dashboard/appointments">
            View All
          </Link>
        </div>

        <div className={Styles.appointmentList}>
          {appointments.map((appointment) => (
            <div key={appointment.doctor + appointment.date} className={Styles.appointmentItem}>
              <div className={Styles.appointmentRow}>
                <div>
                  <h3>{appointment.doctor}</h3>
                  <p>{appointment.specialty}</p>
                </div>
                <span className={`${Styles.statusBadge} ${appointment.status === 'confirmed' ? Styles.confirmed : Styles.pending}`}>
                  {appointment.status}
                </span>
              </div>
              <div className={Styles.appointmentDetails}>
                <div className={Styles.detailItem}>
                  <FontAwesomeIcon icon={faCalendar} />
                  <span>{appointment.date}</span>
                </div>
                <div className={Styles.detailItem}>
                  <FontAwesomeIcon icon={faClock} />
                  <span>{appointment.time}</span>
                </div>
              </div>
            </div>
          ))}
        </div>
      </section>

      <section className={Styles.section}>
        <div className={Styles.sectionHeader}>
          <div>
            <h2>Active Medications</h2>
            <p className={Styles.sectionSubtitle}>Current prescriptions and dosing schedule.</p>
          </div>
          <span className={Styles.sectionTag}>Since 2026</span>
        </div>

        <div className={Styles.medicationList}>
          {medications.map((medication) => (
            <div key={medication.name} className={Styles.medicationCard}>
              <div>
                <h3>{medication.name}</h3>
                <p>{medication.frequency}</p>
              </div>
              <div className={Styles.medicationSince}>Since {medication.since}</div>
            </div>
          ))}
        </div>
      </section>
    </div>
  );
}

export default Dashboard;