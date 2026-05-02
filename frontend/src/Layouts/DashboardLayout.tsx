import { Outlet, Link, useLocation } from "react-router-dom";

import Styles from './DashboardLayout.module.css'


function DashboardLayout() {
    const location = useLocation();
    const menu = [
        { name: "Dashboard", path: "/dashboard" },
        { name: "Appointments", path: "/dashboard/appointments" },
        { name: "Find Doctor", path: "/dashboard/book-appointment" },
    ];
    return (
        <>
            <div className={Styles.Layout}>
                <aside className={Styles.sidebar}>
                    <div className={Styles.sidebarContent}>
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

                    </div>
                </aside>

                <main className={Styles.content}>
                    <Outlet />
                </main>
            </div>
        </>
    );
}

export default DashboardLayout;