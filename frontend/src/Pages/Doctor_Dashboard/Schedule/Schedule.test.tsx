import { render, screen, fireEvent } from '@testing-library/react';
import Schedule from './Schedule';
describe('Schedule Component', () => {

    test('renders schedule header correctly', () => {
        render(<Schedule />);

        expect(screen.getByText(/Welcome, Dr.Name/i)).toBeInTheDocument();
        expect(screen.getByText(/Schedule For:/i)).toBeInTheDocument();
    });

    test('renders appointments count', () => {
        render(<Schedule />);

        expect(screen.getByText(/Appointments/i)).toBeInTheDocument();
    });

    test('opens modal when Cancel All is clicked', () => {
        render(<Schedule />);

        const button = screen.getByText('Cancel All');
        fireEvent.click(button);

        expect(screen.getByText(/Cancel All Appointments/i)).toBeInTheDocument();
    });

    test('closes modal when Close is clicked', () => {
        render(<Schedule />);

        fireEvent.click(screen.getByText('Cancel All'));

        const closeBtn = screen.getByText('Close');
        fireEvent.click(closeBtn);

        expect(screen.queryByText(/Cancel All Appointments/i)).not.toBeInTheDocument();
    });

    test('cancels all pending appointments', () => {
    render(<Schedule />);

    fireEvent.click(screen.getByText('Cancel All'));
    fireEvent.click(screen.getByText('Yes, Cancel'));

    expect(screen.queryByText(/Pending/i)).not.toBeInTheDocument();
    });

});