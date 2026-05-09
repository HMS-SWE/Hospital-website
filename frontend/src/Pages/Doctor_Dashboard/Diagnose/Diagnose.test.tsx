import { render, screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { describe, test, expect, vi } from 'vitest';
import Diagnose from './Diagnose';

describe('Diagnose Component', () => {

    test('renders patient name correctly', () => {
        render(<Diagnose patientName="Mohamed" />);

        expect(screen.getByDisplayValue('Mohamed')).toBeInTheDocument();
    });

    test('shows validation errors when fields are empty', async () => {
        render(<Diagnose patientName="Mohamed" />);

        const saveButton = screen.getByRole('button', {
            name: /save diagnosis/i
        });

        await userEvent.click(saveButton);

        expect(
            screen.getByText(/diagnosis is required/i)
        ).toBeInTheDocument();

        expect(
            screen.getByText(/medications are required/i)
        ).toBeInTheDocument();

        expect(
            screen.getByText(/treatment plan is required/i)
        ).toBeInTheDocument();
    });

    test('does not show validation errors when form is filled', async () => {
        render(<Diagnose patientName="Mohamed" />);

        const diagnosisInput = screen.getByLabelText(/diagnosis/i);
        const medicationTextarea = screen.getByLabelText(/medications/i);
        const treatmentTextarea = screen.getByLabelText(/treatement plan/i);

        await userEvent.type(diagnosisInput, 'Flu');
        await userEvent.type(medicationTextarea, 'Panadol');
        await userEvent.type(treatmentTextarea, 'Rest for 5 days');

        const saveButton = screen.getByRole('button', {
            name: /save diagnosis/i
        });

        await userEvent.click(saveButton);

        expect(
            screen.queryByText(/diagnosis is required/i)
        ).not.toBeInTheDocument();
    });

    test('shows leave modal when back button is clicked with unsaved changes', async () => {
        render(<Diagnose patientName="Mohamed" />);

        const diagnosisInput = screen.getByLabelText(/diagnosis/i);

        await userEvent.type(diagnosisInput, 'Flu');

        const backButton = screen.getByRole('button', {
            name: /back/i
        });

        await userEvent.click(backButton);

        expect(
            screen.getByText(/unsaved changes/i)
        ).toBeInTheDocument();

        expect(
            screen.getByText(/are you sure you want to leave/i)
        ).toBeInTheDocument();
    });

    test('does not show modal when no changes exist', async () => {
        const backSpy = vi.spyOn(window.history, 'back');

        render(<Diagnose patientName="Mohamed" />);

        const backButton = screen.getByRole('button', {
            name: /back/i
        });

        await userEvent.click(backButton);

        expect(
            screen.queryByText(/unsaved changes/i)
        ).not.toBeInTheDocument();

        expect(backSpy).toHaveBeenCalled();
    });

    test('clicking stay closes modal', async () => {
        render(<Diagnose patientName="Mohamed" />);

        const diagnosisInput = screen.getByLabelText(/diagnosis/i);

        await userEvent.type(diagnosisInput, 'Flu');

        const backButton = screen.getByRole('button', {
            name: /back/i
        });

        await userEvent.click(backButton);

        const stayButton = screen.getByRole('button', {
            name: /stay/i
        });

        await userEvent.click(stayButton);

        expect(
            screen.queryByText(/unsaved changes/i)
        ).not.toBeInTheDocument();
    });

});