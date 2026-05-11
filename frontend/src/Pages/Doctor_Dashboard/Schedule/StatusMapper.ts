export function mapAppointmentStatus(status: string): string {
    switch (status) {
        case "CONFIRMED":
            return "Pending";
        case "COMPLETED":
            return "Completed";
        case "CANCELLED":
        case "NOSHOW":
            return "Cancelled";
        default:
            return status;
    }
}