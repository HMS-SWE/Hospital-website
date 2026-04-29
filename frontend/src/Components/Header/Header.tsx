import { faHospital, faSearch } from "@fortawesome/free-solid-svg-icons";
import Contact from "../Contact/Contact";
import Input from "../Input/input";
import SocialLink from "../SocialLink/SocialLink";
import Styles from './Header.module.css'
import { faFacebook } from "@fortawesome/free-brands-svg-icons";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";


function Header(){
    return(
        <>
        <div className={Styles.header}>
            <div className={Styles.headerContents}>
                <div className={Styles.headerTitle}>
                    <div className={Styles.TitleLHS}>
                        <SocialLink 
                            icon={faHospital} 
                            className={Styles.headerSocial}
                            iconClassName={Styles.headerSocialIcon}
                            />
                    </div>
                    <div className={Styles.TitleRHS}>
                        <h3>MediCare HMS</h3>
                        <span>Hospital Management System</span>
                    </div>
                </div>
                <div className={Styles.headerLinks}>
                    <button className={Styles.headerLink}>Home</button>
                    <button className={Styles.headerLink}>Doctors</button>
                    <button className={Styles.headerLink}>Appointments</button>
                    <button className={Styles.headerLink}>Medical Records</button>
                </div>
                <div className={Styles.headerSearch}>
                    <button className={Styles.headerLink}>Contact</button>
                    <form>
                        <div className={Styles.searchField}>
                            <FontAwesomeIcon icon={faSearch} className={Styles.searchIcon}></FontAwesomeIcon>
                            <input type="text" placeholder= "Search doctors, departments, ..." />
                        </div>                    
                    </form>
                </div>
                <div className={Styles.headerButtons}>
                    <button className={Styles.headerLink}>Login</button>
                    <button className={Styles.headerLink}>Register</button>
                </div>
            </div>

        </div>
        </>
    );
}

export default Header;