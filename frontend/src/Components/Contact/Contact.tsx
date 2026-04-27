import Styles from './Contact.module.css'


function Contact({Title, Value, Note}){
    return(
        <>
        <div className={Styles.Contact}>
            <div className='LHS'></div>
            <div className='RHS'>
                <div className={Styles.Title}>{Title}</div>    
                <div className={Styles.Value}>{Value} </div>           
                <div className={Styles.Note}>{Note} </div>
            </div>
        </div>
        </>
    );  
}

export default Contact;