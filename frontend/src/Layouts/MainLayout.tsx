import Footer from "../Components/Footer/Footer";
import Header from "../Components/Header";
import { Outlet } from "react-router-dom";

function MainLayout(){
    return(
        <>  
           <Header />
           <Outlet />
           <Footer /> 
        </>
    );
}

export default MainLayout;