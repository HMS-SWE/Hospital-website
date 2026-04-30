export type Doctor = {
  id: string;
  name: string;
  department: string;
  specialty: string;
  degree: string;
  price: number;
  availableSlots: string[];
};

export const doctors: Doctor[] = [
  {
    id: 'doc-001',
    name: 'Dr. Amina Patel',
    department: 'Cardiology',
    specialty: 'Interventional Cardiology',
    degree: 'MD, FACC',
    price: 150,
    availableSlots: ['2026-05-02T09:00', '2026-05-02T11:00', '2026-05-03T14:00'],
  },
  {
    id: 'doc-002',
    name: 'Dr. Michael Ross',
    department: 'Cardiology',
    specialty: 'Heart Failure Management',
    degree: 'MD, MSC',
    price: 135,
    availableSlots: ['2026-05-02T10:30', '2026-05-03T09:30', '2026-05-04T13:00'],
  },
  {
    id: 'doc-003',
    name: 'Dr. Elena Chen',
    department: 'Neurology',
    specialty: 'Stroke & Neurocritical Care',
    degree: 'MD, PhD',
    price: 160,
    availableSlots: ['2026-05-01T15:00', '2026-05-02T16:30', '2026-05-04T09:00'],
  },
  {
    id: 'doc-004',
    name: 'Dr. Samuel K. Moore',
    department: 'Neurology',
    specialty: 'Epilepsy',
    degree: 'MD',
    price: 145,
    availableSlots: ['2026-05-01T13:30', '2026-05-03T11:00', '2026-05-05T10:00'],
  },
  {
    id: 'doc-005',
    name: 'Dr. Priya Natarajan',
    department: 'Orthopedics',
    specialty: 'Sports Medicine',
    degree: 'MD, FAAOS',
    price: 120,
    availableSlots: ['2026-05-02T08:30', '2026-05-03T12:00', '2026-05-04T15:30'],
  },
  {
    id: 'doc-006',
    name: 'Dr. Marcus Green',
    department: 'Orthopedics',
    specialty: 'Joint Replacement',
    degree: 'MD',
    price: 140,
    availableSlots: ['2026-05-02T14:00', '2026-05-04T09:30', '2026-05-05T13:00'],
  },
  {
    id: 'doc-007',
    name: 'Dr. Sofia Alvarez',
    department: 'Dermatology',
    specialty: 'Cosmetic Dermatology',
    degree: 'MD',
    price: 110,
    availableSlots: ['2026-05-01T10:00', '2026-05-03T16:00', '2026-05-05T11:30'],
  },
  {
    id: 'doc-008',
    name: 'Dr. Benjamin Carter',
    department: 'Dermatology',
    specialty: 'Pediatric Dermatology',
    degree: 'MD',
    price: 125,
    availableSlots: ['2026-05-01T09:30', '2026-05-02T15:00', '2026-05-04T11:00'],
  },
];
