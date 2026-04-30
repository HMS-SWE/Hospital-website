import Styles from './Pagination.module.css'

type Props ={
    total: number;
    perPage: number;
    current: number;
    onChange: (page: number) => void;
}


function Pagination({total, perPage, current, onChange}: Props){
    const totalPages = Math.ceil(total/perPage);
    return(
            <>
            <div>
                <button
                    onClick={() => onChange(current - 1)}
                    disabled={current==1}>
                        prev
                </button>

                {
                    Array.from({length: totalPages}, (_,i) =>(
                        <button
                        key={i}
                        onClick={() => onChange(i+1)}
                        style={{
                            fontWeight: current == i+1? "bold" : "normal",
                        }}>
                            {i + 1}
                        </button>
                    ))
                }

                <button
                    onClick={() => onChange(current + 1)}
                    disabled={current== totalPages}>
                        Next
                </button>

            </div>
            </>
    );
}

export default Pagination;