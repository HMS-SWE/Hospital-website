import { faHospital } from "@fortawesome/free-solid-svg-icons";
import SocialLink from "../SocialLink/SocialLink";
import Styles from './Header.module.css';
import { useState, useEffect } from 'react';

function Header() {
    const [Image, setImage] = useState(() => {
        const user = JSON.parse(localStorage.getItem("user") || "{}");
        return user.image || "";
    });

    const [isLoggedIn, setIsLoggedIn] = useState(() => {
        return !!localStorage.getItem("user");
    });

    useEffect(() => {
        const handleAuthChange = () => {
            const user = JSON.parse(localStorage.getItem("user") || "{}");
            setImage(user.image || "");
            setIsLoggedIn(!!localStorage.getItem("user"));
        };

        window.addEventListener("authChange", handleAuthChange);

        return () => {
            window.removeEventListener("authChange", handleAuthChange);
        };
    }, []);

    return (
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
                <div className={Styles.headerButtons}>
                    
                    {isLoggedIn && (
                        <button className={Styles.headerLink}> 
                            {Image && <img className={Styles.HeaderImg} src={Image} alt="profile pic" />} 
                            Profile 
                        </button>
                    )}
                    
                </div>
            </div>
        </div>
        </>
    );
}

export default Header;