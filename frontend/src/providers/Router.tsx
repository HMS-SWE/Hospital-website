import { createBrowserRouter } from "react-router-dom";
import Register from "../Pages/Register";
import MainLayout from "../Layouts/MainLayout";

const router = createBrowserRouter([
    {
        path: '/',
        element: <MainLayout />,
        children: [
            {
                path: '/',
                element: <Register />,
            },
    ]}

]);

export default router;