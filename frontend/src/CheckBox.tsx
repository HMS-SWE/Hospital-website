


function CheckBox({name, title}){
    return(
        <>
            <input id={name} name={name} type="checkbox"></input>
            <label htmlFor={name}>{title}</label>
            </>
    );
}

export default CheckBox