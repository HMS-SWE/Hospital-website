import { createBrowserRouter } from "react-router-dom";
import Register from "../Pages/Register";
import MainLayout from "../Layouts/MainLayout";
import OAuthCallback from "../Pages/OAuthCallback";

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
    ]}

]);

export default router;