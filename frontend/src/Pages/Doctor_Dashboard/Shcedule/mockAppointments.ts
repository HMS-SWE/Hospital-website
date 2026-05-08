export type Appointment = {
    id: string;
    patientName: string;
    time: string;
    status: 'Completed' | 'Pending' | 'Cancelled';
    type: 'Follow-up' | 'New'
};

export const mockAppointments: Appointment[] = [
    {
        id: '1',
        patientName: 'Ahmed Hassan',
        time: '10:00 AM',
        status: 'Completed',
        type: 'Follow-up'
    },
    {
        id: '2',
        patientName: 'Sara Mohamed',
        time: '11:30 AM',
        status: 'Pending',
        type: 'New'
    },
    {
        id: '3',
        patientName: 'Omar Ali',
        time: '01:00 PM',
        status: 'Cancelled',
        type: 'New'
    },
];