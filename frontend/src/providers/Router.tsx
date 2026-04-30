import { createBrowserRouter } from "react-router-dom";
import MainLayout from "../Layouts/MainLayout";
import OAuthCallback from "../Pages/OAuthCallback";
import { LoginForm } from "../Components/LoginForm";
import Register from "../Pages/Register";

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
                    path: '/register',
                    element: <Register/>,
                },
    ]}

]);

export default router;