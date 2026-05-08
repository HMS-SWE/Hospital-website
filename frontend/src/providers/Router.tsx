import { createBrowserRouter } from "react-router-dom";
import MainLayout from "../Layouts/MainLayout";
import OAuthCallback from "../Pages/OAuthCallback";
import DashboardLayout from "../Layouts/DashboardLayout";
import Appointments from "../Pages/Dashboard/Appointments/Appointments";
import Dashboard from "../Pages/Dashboard/Dashboard/Dashboard"
import BookAppointment from "../Pages/Dashboard/BookAppointment/BookAppointment";
import { LoginForm } from "../Components/LoginForm";
import Register from "../Pages/Register";
import Schedule from "../Pages/Doctor_Dashboard/Shcedule/Schedule"

const router = createBrowserRouter([
    {
        path: '/',
        element: <MainLayout />,
            children: [
                {
                    path: '/',
                    element: <LoginForm />,
                },
                {
                path: '/login',
                element: <LoginForm />,
            },
            {
                    path: "/oauth2/callback",
                    element: <OAuthCallback />,
                },
                {
                    path: "dashboard",
                    element: <DashboardLayout role="patient" />,
                    children: [
                        {index: true, element: <Dashboard />},
                        {path: "appointments", element: <Appointments />},
                        {path: "book-appointment", element: <BookAppointment /> }
                    ]
                },
                {
                    path: "doctor",
                    element: <DashboardLayout role="doctor" />,
                    children: [
                        {index: true, element: <Schedule />},
                    ]
                },
                {
                    path: '/register',
                    element: <Register/>,
                },
                {
                path: "dashboard",
                element: <DashboardLayout />,
                children: [
                    { index: true, element: <Dashboard /> },
                    { path: "appointments", element: <Appointments /> },
                    { path: "book-appointment", element: <BookAppointment /> },
                ]
            },
    ]}

]);
export default router;