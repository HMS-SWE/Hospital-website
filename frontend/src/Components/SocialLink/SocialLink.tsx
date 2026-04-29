import Styles from './SocialLink.module.css'
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

type IconProps = {
  icon: any;
  className?: string;
  iconClassName?: string;
};

function SocialLink({ icon, className = "", iconClassName = "" }: IconProps) {
    return (
        <div className={`${Styles.box} ${className}`}>
            <FontAwesomeIcon 
                className={`${Styles.Icon} ${iconClassName}`} 
                icon={icon} 
                size='2x'
            />
        </div>
    );
}

export default SocialLink;