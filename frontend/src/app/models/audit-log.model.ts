export type AuditAction = 'CREATED' | 'UPDATED';

export interface AuditLog {
  id: number;
  timestamp: string;
  action: AuditAction;
  associateId: string;
  associateName: string;
  fieldChanges: string | null;
  performedBy: string;
}

export function auditActionLabel(action: AuditAction): string {
  return action === 'CREATED' ? 'Created' : 'Updated';
}
