import Styles from './Contact.module.css'
import SocialLink from '../SocialLink/SocialLink';

function Contact({icon, Title, Value, Note}){
    return(
        <>
        <div className={Styles.Contact}>
            <div className={Styles.LHS}>
                <SocialLink icon={icon} />
            </div>
            <div className={Styles.RHS}>
                <div className={Styles.Title}>{Title}</div>    
                <div className={Styles.Value}>{Value} </div>           
                <div className={Styles.Note}>{Note} </div>
            </div>
        </div>
        </>
    );  
}

export default Contact;