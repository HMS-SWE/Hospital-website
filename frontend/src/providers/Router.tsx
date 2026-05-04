import { createBrowserRouter } from "react-router-dom";
import MainLayout from "../Layouts/MainLayout";
import OAuthCallback from "../Pages/OAuthCallback";
import DashboardLayout from "../Layouts/DashboardLayout";
import Appointments from "../Pages/Dashboard/Appointments/Appointments";
import Dashboard from "../Pages/Dashboard/Dashboard/Dashboard"
import BookAppointment from "../Pages/Dashboard/BookAppointment/BookAppointment";
import { LoginForm } from "../Components/LoginForm";
import Register from "../Pages/Register";
import { AuthGuard } from "../Components/AuthGuard";

const router = createBrowserRouter([
    {
        path: '/',
        element: <MainLayout />,
            children: [
                {
                    path: '/',
                    element: <LoginForm />,
                },{
                    path: "/oauth-callback",   // add this
                    element: <OAuthCallback />,
                },
                {
                    path: "dashboard",
                    element: (
                        <AuthGuard>
                            <DashboardLayout />
                        </AuthGuard>
                    ),
                    children: [
                        {index: true, element: <Dashboard />},
                        {path: "appointments", element: <Appointments />},
                        {path: "book-appointment", element: <BookAppointment /> }
                    ]
                },{
                    path: '/register',
                    element: <Register/>,
                },
    ]}

]);

export default router;