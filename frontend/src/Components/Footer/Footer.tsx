import Styles from './Footer.module.css'
import Contact from '../Contact/Contact'
import SocialLink from '../SocialLink/SocialLink';
function Footer() {
    return (
        <>
            <div className={Styles.Footer}>
                <div className={Styles.Container}>
                    <div className={Styles.FooterTitle}>
                        <div className={Styles.TitleLogo}>
                            <div></div>
                            <h4>Hospital Management System</h4>
                        </div>
                        <div className={Styles.description}>
                            <span>Streamline your healthcare operations with our comprehensive patient management platform</span>
                        </div>
                        <div className={Styles.SocialLinks}>
                            
                        </div>
                    </div>

                    <div className={Styles.QuickLinks}>
                        <ul>Quick Links
                            <li>Find a doctor</li>
                            <li>Book Appointment</li>
                            <li>Departments</li>
                            <li>Login</li>
                            <li>Register</li>
                            <li>Careers</li>
                        </ul>
                    </div>

                    <div className={Styles.ContactUs}>
                         <h5>Contact Us</h5>
                <Contact Title="Emergency Contact"  Value="+0123456789" Note="24/7 Available" />
                    </div>

                    <div className={Styles.SuportFAQs}>
                        <ul>Support and help
                            <li>FAQs</li>
                            <li>Help Center</li>
                            <li>Privacy Policy</li>
                            <li>Terms of Service</li>
                            <li>Accessibility</li>
                        </ul>
                    </div>


                </div>
            </div>
        </>
    );
}

export default Footer;