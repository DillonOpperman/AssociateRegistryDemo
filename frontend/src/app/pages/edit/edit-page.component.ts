import { Component, inject, OnInit, signal } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { AssociateFormComponent } from '../../components/associate-form/associate-form.component';
import { Associate, AssociatePayload } from '../../models/associate.model';
import { AssociateService } from '../../services/associate.service';
import { readApiError } from '../../shared/read-api-error';

@Component({
  selector: 'app-edit-page',
  imports: [AssociateFormComponent],
  templateUrl: './edit-page.html',
})
export class EditPageComponent implements OnInit {
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly associateService = inject(AssociateService);

  readonly loading = signal(true);
  readonly submitting = signal(false);
  readonly errorMessage = signal<string | null>(null);
  readonly serverMessage = signal<string | null>(null);
  readonly fieldErrors = signal<Record<string, string>>({});
  readonly associate = signal<Associate | null>(null);

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

  save(payload: AssociatePayload): void {
    const current = this.associate();
    if (!current) {
      return;
    }
    this.submitting.set(true);
    this.serverMessage.set(null);
    this.fieldErrors.set({});
    this.associateService.update(current.id, payload).subscribe({
      next: (associate) => {
        void this.router.navigate(['/associates', associate.id]);
      },
      error: (error: unknown) => {
        const details = readApiError(error);
        this.serverMessage.set(details.message);
        this.fieldErrors.set(details.fieldErrors);
        this.submitting.set(false);
      },
    });
  }

  cancel(): void {
    const current = this.associate();
    void this.router.navigate(current ? ['/associates', current.id] : ['/associates']);
  }
}
