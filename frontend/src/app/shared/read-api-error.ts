import { HttpErrorResponse } from '@angular/common/http';

export interface ApiErrorDetails {
  message: string;
  fieldErrors: Record<string, string>;
}

export function readApiError(error: unknown): ApiErrorDetails {
  if (error instanceof HttpErrorResponse) {
    if (error.status === 0) {
      return {
        message: 'Cannot reach the API. Start the backend on port 8080 and refresh this page.',
        fieldErrors: {},
      };
    }
    const body = error.error as { message?: string; fieldErrors?: Record<string, string> } | null;
    if (body && typeof body === 'object') {
      return {
        message: body.message ?? 'The request could not be completed.',
        fieldErrors: body.fieldErrors ?? {},
      };
    }
  }
  return { message: 'Something went wrong. Try again.', fieldErrors: {} };
}
