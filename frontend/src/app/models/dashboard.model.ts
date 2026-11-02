import { Associate } from './associate.model';
import { AuditLog } from './audit-log.model';

export interface DashboardStats {
  totalAssociates: number;
  activeAssociates: number;
  inactiveAssociates: number;
  onLeaveAssociates: number;
  recentChanges: AuditLog[];
  recentRegistrations: Associate[];
}
