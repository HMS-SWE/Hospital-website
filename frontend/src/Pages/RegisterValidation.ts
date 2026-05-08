import type {RegisterFormData}  from "./Register";


type RegisterFormErrors = Partial<Record<keyof RegisterFormData, string>>;

export const validateRegister = (data: RegisterFormData) => {
    const errors: RegisterFormErrors = {};

    if (!data.firstName) errors.firstName = "This field is required";
    else if (data.firstName.length > 15) errors.firstName = "Too long";

    if (!data.middleName) errors.middleName = "This field is required";
    else if (data.middleName.length > 15) errors.middleName = "Too long";

    if (!data.lastName) errors.lastName = "This field is required";
    else if (data.lastName.length > 15) errors.lastName = "Too long";

    if (!data.dob) {
        errors.dob = "This field is required";
    } else {
        const dobDate = new Date(data.dob);
        const today = new Date();
        today.setHours(0, 0, 0, 0);
        if (dobDate > today) {
            errors.dob = "Date of birth cannot be in the future";
        }
    }

    if (!data.nationalId) {
        errors.nationalId = "This field is required";
    } else if (!/^\d{14}$/.test(data.nationalId)) {
        errors.nationalId = "National ID must be exactly 14 digits";
    }

    if (!data.gender) errors.gender = "This field is required";

    if (!data.email.match(/^[^\s@]+@[^\s@]+\.[^\s@]+$/))
        errors.email = "Invalid email";

    if (!data.phone) errors.phone = "This field is required";
    else if (data.phone.length != 11) errors.phone = "Invalid phone number";

    if (!data.emergency) errors.emergency = "This field is required";
    else if (data.emergency.length != 11) errors.emergency = "Invalid phone number";
    else if (data.emergency === data.phone)
        errors.emergency = "Use a different phone number";

    if (!data.password) {
        errors.password = "This field is required";
    } else if (
        !/^(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&]).{8,}$/.test(data.password)
    ) {
        errors.password =
            "Min 8 chars, 1 uppercase, 1 number, 1 special character";
    }

    if (!data.confirmPassword) {
        errors.confirmPassword = "This field is required";
    } else if (data.password !== data.confirmPassword) {
        errors.confirmPassword = "Passwords do not match";
    }

    return errors;
};