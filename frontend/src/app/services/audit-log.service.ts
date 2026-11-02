import { HttpClient, HttpParams } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';

import { AuditLog } from '../models/audit-log.model';
import { PageResponse } from '../models/page.model';
import { API_BASE } from '../shared/api';

@Injectable({ providedIn: 'root' })
export class AuditLogService {
  private readonly http = inject(HttpClient);

  list(page: number, size: number): Observable<PageResponse<AuditLog>> {
    const params = new HttpParams().set('page', page).set('size', size);
    return this.http.get<PageResponse<AuditLog>>(`${API_BASE}/audit-logs`, { params });
  }
}
