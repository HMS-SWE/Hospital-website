import { createBrowserRouter } from "react-router-dom";
import Register from "../Pages/Register";
import MainLayout from "../Layouts/MainLayout";
import OAuthCallback from "../Pages/OAuthCallback";
import DashboardLayout from "../Layouts/DashboardLayout";
import Appointments from "../Pages/Dashboard/Appointments/Appointments";
import Dashboard from "../Pages/Dashboard/Dashboard/Dashboard"

const router = createBrowserRouter([
    {
        path: '/',
        element: <MainLayout />,
            children: [
                {
                    path: '/',
                    element: <Register />,
                },{
                    path: "/oauth-callback",   // add this
                    element: <OAuthCallback />,
                },
                {
                    path: "dashboard",
                    element: <DashboardLayout />,
                    children: [
                        {index: true, element: <Dashboard />},
                        {path: "appointments", element: <Appointments />}
                    ]
                }
    ]}

]);

export default router;