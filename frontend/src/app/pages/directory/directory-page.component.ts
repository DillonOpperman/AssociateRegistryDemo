import { Component, DestroyRef, inject, signal } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { MatAnchor } from '@angular/material/button';
import { MatOption } from '@angular/material/core';
import { MatFormField, MatLabel } from '@angular/material/form-field';
import { MatInput } from '@angular/material/input';
import { MatPaginator, PageEvent } from '@angular/material/paginator';
import { MatProgressBar } from '@angular/material/progress-bar';
import { MatSelect } from '@angular/material/select';
import { MatSort, MatSortHeader, Sort } from '@angular/material/sort';
import {
  MatCell,
  MatCellDef,
  MatColumnDef,
  MatHeaderCell,
  MatHeaderCellDef,
  MatHeaderRow,
  MatHeaderRowDef,
  MatRow,
  MatRowDef,
  MatTable,
} from '@angular/material/table';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { catchError, debounceTime, finalize, merge, of, startWith, Subject, switchMap, tap } from 'rxjs';

import { Associate, AssociateQuery, DEPARTMENTS, EmploymentStatus, STATUS_OPTIONS } from '../../models/associate.model';
import { AssociateService } from '../../services/associate.service';
import { RoleService } from '../../services/role.service';
import { formatDate } from '../../shared/format';
import { readApiError } from '../../shared/read-api-error';
import { StatusBadgeComponent } from '../../shared/status-badge.component';

@Component({
  selector: 'app-directory-page',
  imports: [
    ReactiveFormsModule,
    RouterLink,
    MatFormField,
    MatLabel,
    MatInput,
    MatSelect,
    MatOption,
    MatTable,
    MatColumnDef,
    MatHeaderCellDef,
    MatHeaderCell,
    MatCellDef,
    MatCell,
    MatHeaderRowDef,
    MatHeaderRow,
    MatRowDef,
    MatRow,
    MatSort,
    MatSortHeader,
    MatPaginator,
    MatProgressBar,
    MatAnchor,
    StatusBadgeComponent,
  ],
  templateUrl: './directory-page.html',
})
export class DirectoryPageComponent {
  readonly role = inject(RoleService);
  private readonly associateService = inject(AssociateService);
  private readonly route = inject(ActivatedRoute);
  private readonly formBuilder = inject(FormBuilder);
  private readonly reload$ = new Subject<void>();

  readonly loading = signal(true);
  readonly errorMessage = signal<string | null>(null);
  readonly rows = signal<Associate[]>([]);
  readonly totalElements = signal(0);
  readonly departments = signal<string[]>([...DEPARTMENTS]);
  readonly pageIndex = signal(0);
  readonly pageSize = signal(10);
  readonly sortActive = signal('lastName');
  readonly sortDirection = signal<'asc' | 'desc'>('asc');
  readonly statuses = STATUS_OPTIONS;
  readonly columns = [
    'associateId',
    'lastName',
    'email',
    'department',
    'jobTitle',
    'officeLocation',
    'status',
    'registrationDate',
    'actions',
  ];
  readonly formatDate = formatDate;

  readonly filters = this.formBuilder.nonNullable.group({
    search: this.route.snapshot.queryParamMap.get('search') ?? '',
    department: '',
    status: '' as '' | EmploymentStatus,
  });

  constructor() {
    const destroyRef = inject(DestroyRef);
    this.associateService
      .departments()
      .pipe(takeUntilDestroyed(destroyRef))
      .subscribe({
        next: (departments) => {
          if (departments.length > 0) {
            this.departments.set(departments);
          }
        },
      });

    merge(
      this.reload$,
      this.filters.valueChanges.pipe(
        debounceTime(250),
        tap(() => this.pageIndex.set(0)),
      ),
    )
      .pipe(
        startWith(undefined),
        switchMap(() => {
          this.loading.set(true);
          this.errorMessage.set(null);
          return this.associateService.search(this.query()).pipe(
            catchError((error: unknown) => {
              this.errorMessage.set(readApiError(error).message);
              this.rows.set([]);
              this.totalElements.set(0);
              return of(null);
            }),
            finalize(() => this.loading.set(false)),
          );
        }),
        takeUntilDestroyed(destroyRef),
      )
      .subscribe((page) => {
        if (!page) {
          return;
        }
        this.rows.set(page.content);
        this.totalElements.set(page.totalElements);
      });
  }

  onPage(event: PageEvent): void {
    this.pageIndex.set(event.pageIndex);
    this.pageSize.set(event.pageSize);
    this.reload$.next();
  }

  onSort(sort: Sort): void {
    if (!sort.direction) {
      this.sortActive.set('lastName');
      this.sortDirection.set('asc');
    } else {
      this.sortActive.set(sort.active);
      this.sortDirection.set(sort.direction === 'desc' ? 'desc' : 'asc');
    }
    this.pageIndex.set(0);
    this.reload$.next();
  }

  private query(): AssociateQuery {
    const value = this.filters.getRawValue();
    return {
      search: value.search,
      department: value.department,
      status: value.status,
      page: this.pageIndex(),
      size: this.pageSize(),
      sort: `${this.sortActive()},${this.sortDirection()}`,
    };
  }
}
