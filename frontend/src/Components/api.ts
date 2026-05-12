// ─── Toggle this to false when the real backend is ready ─────────────────
const USE_MOCK = true;
// ─────────────────────────────────────────────────────────────────────────

const API_URL = "http://localhost:8080/api";

function getToken(): string | null {
  return localStorage.getItem("token");
}

function authHeaders(): Record<string, string> {
  const token = getToken();
  if (!token) throw new Error("Not authenticated");
  return {
    "Content-Type": "application/json",
    Authorization: `Bearer ${token}`,
  };
}

// ─── Types ──────────────────────────────────────────────────────────────

export type AppointmentResponse = {
  appointmentId: number;
  doctorName: string;
  patientName: string;
  date: string;
  startTime: string;
  endTime: string;
  status: string;
  examinationPrice: number | null;
};

export type DayResponse = {
  dayName: string;
  date: string;
  startTime: string;
  endTime: string;
};

export type SlotResponse = {
  slotId: number;
  startTime: string;
  endTime: string;
};

export type DoctorSearchResult = {
  id: number;
  fullName: string;
  specialtyName: string;
  examinationPrice: number | null;
  department: string | null;
  degree: string | null;
};

// ─── Mock Data ───────────────────────────────────────────────────────────

let mockAppointments: AppointmentResponse[] = [
  {
    appointmentId: 1,
    doctorName: "Dr. Sarah Johnson",
    patientName: "John Doe",
    date: "2026-05-15",
    startTime: "10:00",
    endTime: "10:15",
    status: "CONFIRMED",
    examinationPrice: 150,
  },
  {
    appointmentId: 2,
    doctorName: "Dr. Michael Chen",
    patientName: "John Doe",
    date: "2026-05-20",
    startTime: "14:30",
    endTime: "14:45",
    status: "CONFIRMED",
    examinationPrice: 200,
  },
  {
    appointmentId: 3,
    doctorName: "Dr. Emily Davis",
    patientName: "John Doe",
    date: "2026-04-10",
    startTime: "09:00",
    endTime: "09:15",
    status: "CANCELLED",
    examinationPrice: 100,
  },
];

const mockDoctors: DoctorSearchResult[] = [
  {
    id: 1,
    fullName: "Dr. Sarah Johnson",
    specialtyName: "Cardiology",
    examinationPrice: 150,
    department: "Heart & Vascular",
    degree: "MD, FACC",
  },
  {
    id: 2,
    fullName: "Dr. Michael Chen",
    specialtyName: "General Medicine",
    examinationPrice: 200,
    department: "Internal Medicine",
    degree: "MD",
  },
  {
    id: 3,
    fullName: "Dr. Emily Davis",
    specialtyName: "Dermatology",
    examinationPrice: 100,
    department: "Skin & Aesthetics",
    degree: "MD, FAAD",
  },
  {
    id: 4,
    fullName: "Dr. Ahmed Hassan",
    specialtyName: "Orthopedics",
    examinationPrice: 180,
    department: "Bone & Joint",
    degree: "MD, FACS",
  },
  {
    id: 5,
    fullName: "Dr. Fatima Ali",
    specialtyName: "Pediatrics",
    examinationPrice: 120,
    department: "Children's Health",
    degree: "MD, FAAP",
  },
];

// Generate mock available days (next 7 days, skipping Fridays)
function generateMockDays(): DayResponse[] {
  const days: DayResponse[] = [];
  const dayNames = ["SUNDAY", "MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY", "SATURDAY"];
  const today = new Date();

  for (let i = 1; i <= 10; i++) {
    const d = new Date(today);
    d.setDate(d.getDate() + i);
    const dayName = dayNames[d.getDay()];
    // Skip Friday
    if (dayName === "FRIDAY") continue;
    const dateStr = d.toISOString().split("T")[0];
    days.push({
      dayName,
      date: dateStr,
      startTime: "09:00",
      endTime: "17:00",
    });
    if (days.length >= 6) break;
  }
  return days;
}

let mockSlotIdCounter = 100;

// Generate mock time slots for a given date
function generateMockSlots(): SlotResponse[] {
  const slots: SlotResponse[] = [];
  const startHour = 9;
  const endHour = 16;

  for (let h = startHour; h <= endHour; h++) {
    slots.push({
      slotId: mockSlotIdCounter++,
      startTime: `${String(h).padStart(2, "0")}:00`,
      endTime: `${String(h).padStart(2, "0")}:15`,
    });
    slots.push({
      slotId: mockSlotIdCounter++,
      startTime: `${String(h).padStart(2, "0")}:30`,
      endTime: `${String(h).padStart(2, "0")}:45`,
    });
  }
  return slots;
}

// Simulate network delay
function delay(ms = 400): Promise<void> {
  return new Promise((resolve) => setTimeout(resolve, ms));
}

// ─── Appointments ────────────────────────────────────────────────────────

export async function getMyAppointments(): Promise<AppointmentResponse[]> {
  if (USE_MOCK) {
    await delay();
    return [...mockAppointments];
  }

  const res = await fetch(`${API_URL}/appointments/myAppointments`, {
    headers: authHeaders(),
  });
  if (!res.ok) {
    const data = await res.json().catch(() => null);
    throw new Error(data?.message || data || "Failed to fetch appointments");
  }
  return res.json();
}

export async function bookAppointment(slotId: number): Promise<void> {
  if (USE_MOCK) {
    await delay();
    const newId = Math.max(...mockAppointments.map((a) => a.appointmentId), 0) + 1;
    mockAppointments.push({
      appointmentId: newId,
      doctorName: "Dr. Sarah Johnson",
      patientName: "John Doe",
      date: new Date(Date.now() + 7 * 86400000).toISOString().split("T")[0],
      startTime: "10:00",
      endTime: "10:15",
      status: "CONFIRMED",
      examinationPrice: 150,
    });
    return;
  }

  const res = await fetch(`${API_URL}/appointments/book`, {
    method: "POST",
    headers: authHeaders(),
    body: JSON.stringify({ slotId }),
  });
  if (!res.ok) {
    const data = await res.text().catch(() => null);
    throw new Error(data || "Failed to book appointment");
  }
}

export async function cancelAppointment(
  appointmentId: number,
  reason: string
): Promise<void> {
  if (USE_MOCK) {
    await delay();
    const appt = mockAppointments.find((a) => a.appointmentId === appointmentId);
    if (!appt) throw new Error("Appointment not found");
    if (appt.status === "CANCELLED") throw new Error("Already cancelled");
    appt.status = "CANCELLED";
    return;
  }

  const res = await fetch(`${API_URL}/appointments/cancel`, {
    method: "POST",
    headers: authHeaders(),
    body: JSON.stringify({ appointmentId, reason }),
  });
  if (!res.ok) {
    const data = await res.text().catch(() => null);
    throw new Error(data || "Failed to cancel appointment");
  }
}

export async function editAppointment(
  appointmentId: number,
  newSlotId: number
): Promise<void> {
  if (USE_MOCK) {
    await delay();
    const appt = mockAppointments.find((a) => a.appointmentId === appointmentId);
    if (!appt) throw new Error("Appointment not found");
    if (appt.status === "CANCELLED") throw new Error("Cannot edit cancelled appointment");
    // Simulate rescheduling — change the date/time
    const newDate = new Date(Date.now() + 5 * 86400000);
    appt.date = newDate.toISOString().split("T")[0];
    appt.startTime = "11:00";
    appt.endTime = "11:15";
    return;
  }

  const res = await fetch(`${API_URL}/appointments/edit`, {
    method: "POST",
    headers: authHeaders(),
    body: JSON.stringify({ appointmentId, newSlotId }),
  });
  if (!res.ok) {
    const data = await res.text().catch(() => null);
    throw new Error(data || "Failed to reschedule appointment");
  }
}

// ─── Schedules ───────────────────────────────────────────────────────────

export async function getAvailableDays(
  doctorId: number
): Promise<DayResponse[]> {
  if (USE_MOCK) {
    await delay();
    return generateMockDays();
  }

  const res = await fetch(
    `${API_URL}/schedules/doctor/${doctorId}/available-days`,
    { headers: authHeaders() }
  );
  if (!res.ok) {
    const data = await res.text().catch(() => null);
    throw new Error(data || "Failed to fetch available days");
  }
  return res.json();
}

export async function getAvailableSlots(
  doctorId: number,
  date: string
): Promise<SlotResponse[]> {
  if (USE_MOCK) {
    await delay();
    return generateMockSlots();
  }

  const res = await fetch(
    `${API_URL}/schedules/doctor/${doctorId}/available-slots?date=${date}`,
    { headers: authHeaders() }
  );
  if (!res.ok) {
    const data = await res.text().catch(() => null);
    throw new Error(data || "Failed to fetch available slots");
  }
  return res.json();
}

// ─── Doctor Search ───────────────────────────────────────────────────────

export async function searchDoctors(
  query: string = ""
): Promise<DoctorSearchResult[]> {
  if (USE_MOCK) {
    await delay();
    if (!query.trim()) return [...mockDoctors];
    const q = query.toLowerCase();
    return mockDoctors.filter(
      (d) =>
        d.fullName.toLowerCase().includes(q) ||
        d.specialtyName.toLowerCase().includes(q)
    );
  }

  const res = await fetch(
    `${API_URL}/doctors/search?query=${encodeURIComponent(query)}`,
    { headers: authHeaders() }
  );
  if (!res.ok) {
    const data = await res.text().catch(() => null);
    throw new Error(data || "Failed to search doctors");
  }
  return res.json();
}
