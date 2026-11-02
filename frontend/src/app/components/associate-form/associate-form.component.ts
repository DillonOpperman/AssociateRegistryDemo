import { Component, effect, inject, input, output } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatButton } from '@angular/material/button';
import { MatOption } from '@angular/material/core';
import { MatError, MatFormField, MatHint, MatLabel } from '@angular/material/form-field';
import { MatInput } from '@angular/material/input';
import { MatSelect } from '@angular/material/select';

import {
  Associate,
  AssociatePayload,
  DEPARTMENTS,
  EmploymentStatus,
  OFFICES,
  STATUS_OPTIONS,
} from '../../models/associate.model';

@Component({
  selector: 'app-associate-form',
  imports: [
    ReactiveFormsModule,
    MatFormField,
    MatLabel,
    MatError,
    MatHint,
    MatInput,
    MatSelect,
    MatOption,
    MatButton,
  ],
  templateUrl: './associate-form.html',
})
export class AssociateFormComponent {
  readonly mode = input<'create' | 'edit'>('create');
  readonly associate = input<Associate | null>(null);
  readonly submitting = input(false);
  readonly serverMessage = input<string | null>(null);
  readonly fieldErrors = input<Record<string, string>>({});

  readonly submitted = output<AssociatePayload>();
  readonly cancelled = output<void>();

  readonly departments = DEPARTMENTS;
  readonly offices = OFFICES;
  readonly statuses = STATUS_OPTIONS;

  private readonly formBuilder = inject(FormBuilder);

  readonly form = this.formBuilder.nonNullable.group({
    associateId: ['', [Validators.required, Validators.pattern(/^AR-\d{5}$/i)]],
    firstName: ['', [Validators.required, Validators.maxLength(60)]],
    lastName: ['', [Validators.required, Validators.maxLength(60)]],
    email: ['', [Validators.required, Validators.email, Validators.maxLength(120)]],
    phoneNumber: ['', [Validators.maxLength(30), Validators.pattern(/^$|^[+0-9().\-\s]{7,30}$/)]],
    department: ['', Validators.required],
    jobTitle: ['', [Validators.required, Validators.maxLength(80)]],
    officeLocation: ['', Validators.required],
    status: ['ACTIVE' as EmploymentStatus, Validators.required],
  });

  constructor() {
    effect(() => {
      const associate = this.associate();
      if (!associate) {
        return;
      }
      this.form.patchValue(
        {
          associateId: associate.associateId,
          firstName: associate.firstName,
          lastName: associate.lastName,
          email: associate.email,
          phoneNumber: associate.phoneNumber ?? '',
          department: associate.department,
          jobTitle: associate.jobTitle,
          officeLocation: associate.officeLocation,
          status: associate.status,
        },
        { emitEvent: false },
      );
    });

    effect(() => {
      const errors = this.fieldErrors();
      for (const name of Object.keys(this.form.controls)) {
        const control = this.form.get(name);
        if (!control) {
          continue;
        }
        const serverMessage = errors[name];
        const current = control.errors;
        if (serverMessage) {
          control.setErrors({ ...(current ?? {}), server: serverMessage });
          control.markAsTouched();
        } else if (current && current['server']) {
          const rest = { ...current };
          delete rest['server'];
          control.setErrors(Object.keys(rest).length ? rest : null);
        }
      }
    });
  }

  submit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    const value = this.form.getRawValue();
    this.submitted.emit({
      associateId: value.associateId.trim().toUpperCase(),
      firstName: value.firstName.trim(),
      lastName: value.lastName.trim(),
      email: value.email.trim(),
      phoneNumber: value.phoneNumber.trim(),
      department: value.department,
      jobTitle: value.jobTitle.trim(),
      officeLocation: value.officeLocation,
      status: value.status,
    });
  }

  messageFor(name: string): string {
    const control = this.form.get(name);
    if (!control || !control.errors) {
      return '';
    }
    if (control.errors['server']) {
      return String(control.errors['server']);
    }
    if (!(control.touched || control.dirty)) {
      return '';
    }
    if (control.errors['required']) {
      return `${this.labelFor(name)} is required`;
    }
    if (control.errors['email']) {
      return 'Enter a valid email address';
    }
    if (control.errors['pattern'] && name === 'associateId') {
      return 'Use the format AR-10001';
    }
    if (control.errors['pattern'] && name === 'phoneNumber') {
      return 'Enter a valid phone number or leave the field blank';
    }
    if (control.errors['maxlength']) {
      return `${this.labelFor(name)} is too long`;
    }
    return 'Check this field';
  }

  private labelFor(name: string): string {
    const labels: Record<string, string> = {
      associateId: 'Associate ID',
      firstName: 'First name',
      lastName: 'Last name',
      email: 'Email',
      phoneNumber: 'Phone number',
      department: 'Department',
      jobTitle: 'Job title',
      officeLocation: 'Office location',
      status: 'Status',
    };
    return labels[name] ?? 'This field';
  }
}
