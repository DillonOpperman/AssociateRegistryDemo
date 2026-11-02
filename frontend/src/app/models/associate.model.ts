export type EmploymentStatus = 'ACTIVE' | 'INACTIVE' | 'ON_LEAVE';

export interface Associate {
  id: number;
  associateId: string;
  fullName: string;
  firstName: string;
  lastName: string;
  email: string;
  phoneNumber: string | null;
  department: string;
  jobTitle: string;
  officeLocation: string;
  status: EmploymentStatus;
  registrationDate: string;
  lastUpdatedAt: string;
}

export interface AssociatePayload {
  associateId: string;
  firstName: string;
  lastName: string;
  email: string;
  phoneNumber: string;
  department: string;
  jobTitle: string;
  officeLocation: string;
  status: EmploymentStatus;
}

export interface AssociateQuery {
  search: string;
  department: string;
  status: '' | EmploymentStatus;
  page: number;
  size: number;
  sort: string;
}

export const DEPARTMENTS = [
  'Claims Operations',
  'Underwriting',
  'Customer Experience',
  'Information Technology',
  'Finance',
  'Human Resources',
  'Legal and Compliance',
  'Marketing',
  'Enterprise Operations',
] as const;

export const OFFICES = [
  'North Harbor Office, Seattle',
  'Lakeside Center, Minneapolis',
  'Midtown Plaza, Chicago',
  'Capitol Square, Denver',
  'Riverside Campus, Austin',
  'Peachtree Office, Atlanta',
  'Desert Ridge Center, Phoenix',
  'Harbor Point, Boston',
] as const;

export const STATUS_OPTIONS: { value: EmploymentStatus; label: string }[] = [
  { value: 'ACTIVE', label: 'Active' },
  { value: 'INACTIVE', label: 'Inactive' },
  { value: 'ON_LEAVE', label: 'On Leave' },
];

export function statusLabel(status: EmploymentStatus): string {
  return STATUS_OPTIONS.find((option) => option.value === status)?.label ?? status;
}
