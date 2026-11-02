import { HttpClient, HttpParams } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';

import { Associate, AssociatePayload, AssociateQuery } from '../models/associate.model';
import { PageResponse } from '../models/page.model';
import { API_BASE } from '../shared/api';

@Injectable({ providedIn: 'root' })
export class AssociateService {
  private readonly http = inject(HttpClient);

  search(query: AssociateQuery): Observable<PageResponse<Associate>> {
    let params = new HttpParams().set('page', query.page).set('size', query.size).set('sort', query.sort);
    const search = query.search.trim();
    if (search) {
      params = params.set('search', search);
    }
    if (query.department) {
      params = params.set('department', query.department);
    }
    if (query.status) {
      params = params.set('status', query.status);
    }
    return this.http.get<PageResponse<Associate>>(`${API_BASE}/associates`, { params });
  }

  departments(): Observable<string[]> {
    return this.http.get<string[]>(`${API_BASE}/associates/departments`);
  }

  get(id: number): Observable<Associate> {
    return this.http.get<Associate>(`${API_BASE}/associates/${id}`);
  }

  create(payload: AssociatePayload): Observable<Associate> {
    return this.http.post<Associate>(`${API_BASE}/associates`, payload);
  }

  update(id: number, payload: AssociatePayload): Observable<Associate> {
    return this.http.put<Associate>(`${API_BASE}/associates/${id}`, payload);
  }
}
