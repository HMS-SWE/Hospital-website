import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faCalendar, faFileMedical, faPills, faClock } from '@fortawesome/free-solid-svg-icons';
import { Link } from 'react-router-dom';
import Styles from './Dashboard.module.css';
import { useState, useEffect } from 'react';
import { getMyAppointments } from '../../../Components/api';
import type { AppointmentResponse } from '../../../Components/api';

function Dashboard() {
  const [name] = useState(() => {
    const user = JSON.parse(localStorage.getItem("user") || "{}");
    return user.fullName || user.name || user.userName || "User";
  });

  const [appointments, setAppointments] = useState<AppointmentResponse[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    setLoading(true);
    getMyAppointments()
      .then((data) => {
        setAppointments(data);
      })
      .catch((err) => {
        setError(err instanceof Error ? err.message : 'Failed to load appointments');
      })
      .finally(() => setLoading(false));
  }, []);

  const upcomingAppointments = appointments.filter(
    (a) => a.status !== 'CANCELLED'
  );

  const sortedUpcoming = [...upcomingAppointments].sort((a, b) => {
    const dateCompare = a.date.localeCompare(b.date);
    if (dateCompare !== 0) return dateCompare;
    return a.startTime.localeCompare(b.startTime);
  });

  const nextAppointment = sortedUpcoming[0];

  const formatTime = (time: string) => {
    const [hours, minutes] = time.split(':').map(Number);
    const ampm = hours >= 12 ? 'PM' : 'AM';
    const displayHour = hours % 12 || 12;
    return `${displayHour}:${String(minutes).padStart(2, '0')} ${ampm}`;
  };

  return (
    <div className={Styles.dashboardPage}>
      <header className={Styles.pageHeader}>
        <div>
          <p className={Styles.overline}>Dashboard</p>
          <h1 className={Styles.pageTitle}>Welcome back, {name}</h1>
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
          <div className={Styles.statValue}>{loading ? '...' : upcomingAppointments.length}</div>
          <div className={Styles.statMeta}>
            {nextAppointment ? `Next visit: ${nextAppointment.date}` : 'No upcoming visits'}
          </div>
        </article>

        <article className={Styles.statCard}>
          <div className={Styles.statHeader}>
            <span className={Styles.statIcon}>
              <FontAwesomeIcon icon={faPills} />
            </span>
            <span className={Styles.statLabel}>Active Medications</span>
          </div>
          <div className={Styles.statValue}>—</div>
          <div className={Styles.statMeta}>No data available</div>
        </article>

        <article className={Styles.statCard}>
          <div className={Styles.statHeader}>
            <span className={Styles.statIcon}>
              <FontAwesomeIcon icon={faFileMedical} />
            </span>
            <span className={Styles.statLabel}>Medical Records</span>
          </div>
          <div className={Styles.statValue}>—</div>
          <div className={Styles.statMeta}>No data available</div>
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

        {loading && <p style={{ padding: '16px' }}>Loading appointments...</p>}
        {error && <p style={{ padding: '16px', color: '#c00' }}>{error}</p>}

        <div className={Styles.appointmentList}>
          {!loading && sortedUpcoming.length === 0 && !error && (
            <p style={{ padding: '16px', color: '#888' }}>No upcoming appointments. <Link to="/dashboard/book-appointment">Book one now!</Link></p>
          )}
          {sortedUpcoming.slice(0, 3).map((appointment) => (
            <div key={appointment.appointmentId} className={Styles.appointmentItem}>
              <div className={Styles.appointmentRow}>
                <div>
                  <h3>{appointment.doctorName}</h3>
                  <p>{appointment.examinationPrice != null ? `${appointment.examinationPrice} EGP` : ''}</p>
                </div>
                <span className={`${Styles.statusBadge} ${appointment.status === 'CONFIRMED' ? Styles.confirmed : Styles.pending}`}>
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
                  <span>{formatTime(appointment.startTime)} - {formatTime(appointment.endTime)}</span>
                </div>
              </div>
            </div>
          ))}
        </div>
      </section>
    </div>
  );
}

export default Dashboard;