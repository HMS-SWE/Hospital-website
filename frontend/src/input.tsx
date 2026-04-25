function Input({label, type}){
    return(
        <>
        <div className="Field">
            <h3>{label}</h3>
        <input className="Input" type={type}></input>
        </div>
        </>
    );
}
export default Input