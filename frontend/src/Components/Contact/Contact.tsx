import Styles from './Contact.module.css'
import SocialLink from '../SocialLink/SocialLink';
import type { IconDefinition } from '@fortawesome/fontawesome-svg-core';

type ContactProps = {
    icon: IconDefinition;
    Title: string;
    Value: string;
    Note: string;
}


function Contact({icon, Title, Value, Note}: ContactProps){
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