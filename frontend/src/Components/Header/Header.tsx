import { faHospital } from "@fortawesome/free-solid-svg-icons";
import SocialLink from "../SocialLink/SocialLink";
import Styles from './Header.module.css'
import { useEffect, useState } from 'react';


function Header(){
    const [Image, setImage] = useState("");
    useEffect(() => {
        const user = JSON.parse(localStorage.getItem("user") || "{}");
        setImage(user.image || "");
    }, []);

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
                <div className={Styles.headerButtons}>
                    <button className={Styles.headerLink}> {Image && <img className={Styles.HeaderImg} src={Image} alt="profile pic" />} Profile </button>
                </div>
            </div>

        </div>
        </>
    );
}

export default Header;