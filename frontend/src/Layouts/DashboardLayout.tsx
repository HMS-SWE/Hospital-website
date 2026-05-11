import { Outlet, Link, useLocation } from "react-router-dom";
import Styles from './DashboardLayout.module.css'
import {logout} from '../Components/auth'
import { SidebarLinks, type UserRole } from "./SidebarLinks";


function DashboardLayout() {
    const location = useLocation();
    const handleLogout = async () => {
        try {
            await logout(); 
        } catch (error) {
            console.error("Logout error:", error);
        }
        localStorage.removeItem("user");
        
        window.dispatchEvent(new Event("authChange"));
    };

    const user = JSON.parse(localStorage.getItem("user") || "{}");
    const role = user.role as UserRole;

    const menu = SidebarLinks[role] ?? [];

    return (
        <>
            <div className={Styles.Layout}>
                <aside className={Styles.sidebar}>
                    <div className={Styles.sidebarContent}>
                        <div className={Styles.logo}>
                        <h2>HealthCare</h2>
                        <span>{role}'s Portal</span>
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

                    {}
                    <div className={Styles.logout} onClick={handleLogout}>Logout</div>

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