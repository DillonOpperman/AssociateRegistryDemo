import { Component, inject, OnInit, signal } from '@angular/core';
import { RouterLink } from '@angular/router';

import { auditActionLabel } from '../../models/audit-log.model';
import { DashboardStats } from '../../models/dashboard.model';
import { DashboardService } from '../../services/dashboard.service';
import { formatDate, formatDateTime } from '../../shared/format';
import { readApiError } from '../../shared/read-api-error';
import { StatusBadgeComponent } from '../../shared/status-badge.component';

@Component({
  selector: 'app-dashboard-page',
  imports: [RouterLink, StatusBadgeComponent],
  templateUrl: './dashboard-page.html',
})
export class DashboardPageComponent implements OnInit {
  private readonly dashboardService = inject(DashboardService);

  readonly loading = signal(true);
  readonly errorMessage = signal<string | null>(null);
  readonly stats = signal<DashboardStats | null>(null);
  readonly formatDate = formatDate;
  readonly formatDateTime = formatDateTime;
  readonly auditActionLabel = auditActionLabel;

  ngOnInit(): void {
    this.dashboardService.stats().subscribe({
      next: (stats) => {
        this.stats.set(stats);
        this.loading.set(false);
      },
      error: (error: unknown) => {
        this.errorMessage.set(readApiError(error).message);
        this.loading.set(false);
      },
    });
  }
}
