import { createBrowserRouter } from "react-router-dom";
import MainLayout from "../Layouts/MainLayout";
import OAuthCallback from "../Pages/OAuthCallback";
import DashboardLayout from "../Layouts/DashboardLayout";
import Appointments from "../Pages/Dashboard/Appointments/Appointments";
import Dashboard from "../Pages/Dashboard/Dashboard/Dashboard"
import BookAppointment from "../Pages/Dashboard/BookAppointment/BookAppointment";
import { LoginForm } from "../Components/LoginForm";
import Register from "../Pages/Register";
import MedicalHistory from "../Pages/Doctor_Dashboard/medicalHistory/MedicalHistoy";
import Schedule from "../Pages/Doctor_Dashboard/Schedule/Schedule";
import Diagnose from "../Pages/Doctor_Dashboard/Diagnose/Diagnose";
import PatientProfile from "../Pages/patientProfile/PatientProfile";
import DoctorProfile from "../Pages/DoctorProfile/DoctorProfile";

const router = createBrowserRouter([
    {
        path: '/',
        element: <MainLayout />,
            children: [
                {
                    path: '/',
                    element: <LoginForm />,
                },{
                    path: "/oauth2/callback",
                    element: <OAuthCallback />,
                },
                {
                    path: "dashboard",
                    element: <DashboardLayout/>,
                    children: [
                        {index: true, element: <Dashboard />},
                        {path: "appointments", element: <Appointments />},
                        {path: "book-appointment", element: <BookAppointment /> },
                        {path: "medical-history/:patientId", element: <MedicalHistory /> },
                        {path: "patient-profile", element: <PatientProfile />}
                    ]
                },
                {
                    path: '/register',
                    element: <Register/>,
                },
                {
                    path: "doctor",
                    element: <DashboardLayout />,
                    children: [
                        {index: true, element: <Schedule />},
                        {path: "diagnose/:visitId", element: <Diagnose />},
                        {path: "medical-history/:patientId", element: <MedicalHistory /> },
                        {path: "doctor-profile", element: <DoctorProfile/>}
                    ]
                }
    ]}

]);

export default router;