import { Outlet, Link, useLocation } from "react-router-dom";

import Styles from './DashboardLayout.module.css'


function DashboardLayout() {
    const Location = useLocation();
    const menu = [
        // { name: "Dashboard", path: "/dashboard" },
        { name: "Appointments", path: "/dashboard/appointments" },
        // { name: "Medical History", path: "/dashboard/medical-history" },
        // { name: "Find Doctor", path: "/dashboard/find-doctor" },
        // { name: "Messages", path: "/dashboard/messages" },
        // { name: "Profile", path: "/dashboard/profile" },
    ];
    return (
        <>
            <div className={Styles.Layout}>
                <aside className={Styles.sidebar}>
                    <div className={Styles.logo}>
                        <h2>HealthCare</h2>
                        <span>Patient Portal</span>
                    </div>
                    <div className={Styles.divider}></div>

                    <ul className={Styles.menu}>
                        {menu.map((item) => (
                            <li
                                key={item.path}
                                className={location.pathname === item.path ? "active" : ""}
                            >
                                <Link to={item.path}>{item.name}</Link>
                            </li>
                        ))}
                    </ul>

                    <div className={Styles.logout}>Logout</div>
                </aside>

                <main className={Styles.content}>
                    <Outlet />
                </main>
            </div>
        </>
    );
}

export default DashboardLayout;