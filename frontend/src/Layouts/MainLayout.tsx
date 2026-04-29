import Footer from "../Components/Footer/Footer";
import Header from "../Components/Header/Header";
import { Outlet } from "react-router-dom";
import Styles from './MainLayout.module.css'

function MainLayout(){
    return(
        <>  <div>
                <Header />
                <div className={Styles.mainContent}>
                    <Outlet />
                    <Footer />
                </div>   
            </div>
           
        </>
    );
}

export default MainLayout;