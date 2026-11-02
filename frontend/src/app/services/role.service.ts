import { computed, Injectable, signal } from '@angular/core';

export type DemoRole = 'ADMINISTRATOR' | 'READ_ONLY';

const STORAGE_KEY = 'associate-registry-demo-role';

@Injectable({ providedIn: 'root' })
export class RoleService {
  private readonly roleSignal = signal<DemoRole>(this.readStoredRole());

  readonly role = this.roleSignal.asReadonly();
  readonly isAdmin = computed(() => this.roleSignal() === 'ADMINISTRATOR');
  readonly demoUserLabel = computed(() =>
    this.isAdmin() ? 'Jordan Hale (Administrator)' : 'Riley Chen (Read-Only User)',
  );

  setRole(role: DemoRole): void {
    this.roleSignal.set(role);
    localStorage.setItem(STORAGE_KEY, role);
  }

  private readStoredRole(): DemoRole {
    try {
      return localStorage.getItem(STORAGE_KEY) === 'READ_ONLY' ? 'READ_ONLY' : 'ADMINISTRATOR';
    } catch {
      return 'ADMINISTRATOR';
    }
  }
}
