import { Component, inject, OnInit, signal } from '@angular/core';
import { MatPaginator, PageEvent } from '@angular/material/paginator';
import { MatProgressBar } from '@angular/material/progress-bar';
import { RouterLink } from '@angular/router';

import { auditActionLabel, AuditLog } from '../../models/audit-log.model';
import { AuditLogService } from '../../services/audit-log.service';
import { formatDateTime } from '../../shared/format';
import { readApiError } from '../../shared/read-api-error';

@Component({
  selector: 'app-audit-page',
  imports: [RouterLink, MatPaginator, MatProgressBar],
  templateUrl: './audit-page.html',
})
export class AuditPageComponent implements OnInit {
  private readonly auditLogService = inject(AuditLogService);

  readonly loading = signal(true);
  readonly errorMessage = signal<string | null>(null);
  readonly rows = signal<AuditLog[]>([]);
  readonly totalElements = signal(0);
  readonly pageIndex = signal(0);
  readonly pageSize = signal(20);
  readonly formatDateTime = formatDateTime;
  readonly auditActionLabel = auditActionLabel;

  ngOnInit(): void {
    this.load();
  }

  onPage(event: PageEvent): void {
    this.pageIndex.set(event.pageIndex);
    this.pageSize.set(event.pageSize);
    this.load();
  }

  private load(): void {
    this.loading.set(true);
    this.errorMessage.set(null);
    this.auditLogService.list(this.pageIndex(), this.pageSize()).subscribe({
      next: (page) => {
        this.rows.set(page.content);
        this.totalElements.set(page.totalElements);
        this.loading.set(false);
      },
      error: (error: unknown) => {
        this.errorMessage.set(readApiError(error).message);
        this.rows.set([]);
        this.loading.set(false);
      },
    });
  }
}
