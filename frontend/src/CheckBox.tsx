type CheckBoxProps={
    name: string;
    title: string;
}


function CheckBox({name, title}: CheckBoxProps){
    return(
        <>
            <input id={name} name={name} type="checkbox"></input>
            <label htmlFor={name}>{title}</label>
            </>
    );
}

export default CheckBox