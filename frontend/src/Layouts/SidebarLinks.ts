

export type UserRole = "patient" | "doctor";

export interface MenuItem {
    name: string;
    path: string;
}

export const SidebarLinks: Record<UserRole, MenuItem[]> = {
    
    patient: [
        { name: "Dashboard", path: "/dashboard" },
        { name: "Appointments", path: "/dashboard/appointments" },
        { name: "Find Doctor", path: "/dashboard/book-appointment" },
    ],

    doctor: [
        { name: "Today's Schedule", path: "/doctor" },
        {name: "Profile", path:"/doctor/doctor-profile"}
    ]
};