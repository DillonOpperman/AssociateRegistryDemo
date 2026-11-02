import { Component, inject, OnInit, signal } from '@angular/core';
import { MatAnchor } from '@angular/material/button';
import { ActivatedRoute, RouterLink } from '@angular/router';

import { Associate } from '../../models/associate.model';
import { AssociateService } from '../../services/associate.service';
import { RoleService } from '../../services/role.service';
import { formatDate, formatDateTime } from '../../shared/format';
import { readApiError } from '../../shared/read-api-error';
import { StatusBadgeComponent } from '../../shared/status-badge.component';

@Component({
  selector: 'app-profile-page',
  imports: [RouterLink, MatAnchor, StatusBadgeComponent],
  templateUrl: './profile-page.html',
})
export class ProfilePageComponent implements OnInit {
  readonly role = inject(RoleService);
  private readonly route = inject(ActivatedRoute);
  private readonly associateService = inject(AssociateService);

  readonly loading = signal(true);
  readonly errorMessage = signal<string | null>(null);
  readonly associate = signal<Associate | null>(null);
  readonly formatDate = formatDate;
  readonly formatDateTime = formatDateTime;

  ngOnInit(): void {
    const id = Number(this.route.snapshot.paramMap.get('id'));
    if (!Number.isFinite(id)) {
      this.errorMessage.set('That associate address is not valid.');
      this.loading.set(false);
      return;
    }
    this.associateService.get(id).subscribe({
      next: (associate) => {
        this.associate.set(associate);
        this.loading.set(false);
      },
      error: (error: unknown) => {
        this.errorMessage.set(readApiError(error).message);
        this.loading.set(false);
      },
    });
  }
}
