import { Component, inject, signal } from '@angular/core';
import { Router } from '@angular/router';

import { AssociateFormComponent } from '../../components/associate-form/associate-form.component';
import { AssociatePayload } from '../../models/associate.model';
import { AssociateService } from '../../services/associate.service';
import { readApiError } from '../../shared/read-api-error';

@Component({
  selector: 'app-register-page',
  imports: [AssociateFormComponent],
  templateUrl: './register-page.html',
})
export class RegisterPageComponent {
  private readonly associateService = inject(AssociateService);
  private readonly router = inject(Router);

  readonly submitting = signal(false);
  readonly serverMessage = signal<string | null>(null);
  readonly fieldErrors = signal<Record<string, string>>({});

  save(payload: AssociatePayload): void {
    this.submitting.set(true);
    this.serverMessage.set(null);
    this.fieldErrors.set({});
    this.associateService.create(payload).subscribe({
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
    void this.router.navigate(['/associates']);
  }
}
