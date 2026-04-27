import Styles from './SocialLink.module.css'
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

type IconProps = {
  icon: any;
};

function SocialLink({icon}: IconProps){
    return(
        <>
        <div className={Styles.container}>
                <div className={Styles.box}>
                <FontAwesomeIcon className='Icon' icon={icon} size='2x'/>
                </div>
        </div>
        </>
    );
}

export default SocialLink;