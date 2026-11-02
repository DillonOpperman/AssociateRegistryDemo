import { Component, computed, input } from '@angular/core';

import { EmploymentStatus, statusLabel } from '../models/associate.model';

@Component({
  selector: 'app-status-badge',
  template: `<span class="status-badge" [attr.data-status]="status()">{{ label() }}</span>`,
})
export class StatusBadgeComponent {
  readonly status = input.required<EmploymentStatus>();
  readonly label = computed(() => statusLabel(this.status()));
}
