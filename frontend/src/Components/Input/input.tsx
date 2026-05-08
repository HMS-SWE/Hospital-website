import Styles from './input.module.css'

type InputProps ={
    label: string;
    type: string;
    value: string;
    onChange: (e: React.ChangeEvent<HTMLInputElement>) => void;
    error?: string;
    inputProps?: React.InputHTMLAttributes<HTMLInputElement>;
}

function Input({label, type, value, onChange, error, inputProps}: InputProps){
    return(
        <>
        <div className="Field">
            <h3>{label}</h3>
        <input className={Styles.Input} 
        type={type}
        value={value}
        onChange={onChange}
        {...inputProps}
        ></input>
        <br></br>
        <br></br>
        {error && <span style={{ color: "red" }}>{error}</span>}
        </div>
        </>
    );
}
export default Input