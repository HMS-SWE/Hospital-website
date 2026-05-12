const API_URL = "http://localhost:8080/api";

function getToken(): string | null {
  return localStorage.getItem("token") || localStorage.getItem("jwt");
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

// ─── Appointments ────────────────────────────────────────────────────────

export async function getMyAppointments(): Promise<AppointmentResponse[]> {
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
