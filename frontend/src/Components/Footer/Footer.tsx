import Styles from './Footer.module.css'
import Contact from '../Contact/Contact'
import SocialLink from '../SocialLink/SocialLink';
import { faInstagram, faLinkedin, faSquareFacebook, faTwitter, faYoutube } from '@fortawesome/free-brands-svg-icons';
import { faPhone, faLocationPin, faEnvelope} from '@fortawesome/free-solid-svg-icons'
function Footer() {
    return (
        <>
            <div className={Styles.Footer}>
                <div className={Styles.FooterContainer}>
                    
                    <div className={Styles.FooterTitle}>

                        <div className={Styles.TitleLogo}>
                            <div>

                            </div>
                            <h4>Hospital Management System</h4>
                        </div>

                        <div className={Styles.description}>
                            <span>A comprehensive hospital management system designed to streamline patient care, appointments,
                                and medical records. Trusted by healthcare professionals worldwide.</span>
                        </div>

                        <div className={Styles.SocialLinks}>
                            <SocialLink icon={faSquareFacebook} />
                            <SocialLink icon={faTwitter} />
                            <SocialLink icon={faInstagram} />
                            <SocialLink icon={faLinkedin} />
                            <SocialLink icon={faYoutube} />
                        </div>

                    </div>

                    <div className={Styles.QuickLinks}>
                        <h3>Quick Links</h3>
                        <ul>
                            <li>Find a doctor</li>
                            <li>Book Appointment</li>
                            <li>Departments</li>
                            <li>Login</li>
                            <li>Register</li>
                            <li>Careers</li>
                        </ul>
                    </div>            

                    <div className={Styles.ContactUs}>
                        <h3>Contact Us</h3>
                        <Contact icon={faPhone} Title="Emergency Contact" Value="+0123456789" Note="24/7 Available" /><br></br>
                        <Contact icon={faEnvelope} Title="Email Support" Value="support@medicare.com" Note="" /><br></br>
                        <Contact icon={faLocationPin} Title="Address" Value="123 Healthcare Avenue," Note="Medical District, NY 10001" />
                    </div>

                    <div className={Styles.SuportFAQs}>
                        <h3>Support and help</h3>
                        <ul>
                            <li>FAQs</li>
                            <li>Help Center</li>
                            <li>Privacy Policy</li>
                            <li>Terms of Service</li>
                            <li>Accessibility</li>
                        </ul>
                        </div>
                    <div className={Styles.divider}></div>

                </div>

            </div>
                
        </>
    );
}

export default Footer;