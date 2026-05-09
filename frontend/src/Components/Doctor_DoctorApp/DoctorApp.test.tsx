import { render, screen } from '@testing-library/react'
import DoctorApp from './DoctorApp'

test('Appointments details are displayed correctly',() =>{
    render(
        <DoctorApp patientName='Alice' 
                    type='New' 
                    time='3:15 PM' 
                    status='Cancelled' 
                    id='5' />
    )
    expect(screen.getByText('Alice')).toBeInTheDocument()
    expect(screen.getByText('3:15 PM')).toBeInTheDocument()
    expect(screen.getByText('New')).toBeInTheDocument()
    expect(screen.getByText('Cancelled')).toBeInTheDocument()

})

