import Footer from "../Components/Footer/Footer";
import Header from "../Components/Header";
import { Outlet } from "react-router-dom";

function MainLayout(){
    return(
        <>  <div>
                <Header />
                <Outlet />
                <Footer />   
            </div>
           
        </>
    );
}

export default MainLayout;