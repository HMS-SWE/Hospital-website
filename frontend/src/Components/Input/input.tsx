import Styles from './input.module.css'

function Input({label, type}){
    return(
        <>
        <div className="Field">
            <h3>{label}</h3>
        <input className={Styles.Input} type={type}></input>
        </div>
        </>
    );
}
export default Input