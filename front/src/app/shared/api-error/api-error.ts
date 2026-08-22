import { HttpErrorResponse } from '@angular/common/http';
import { errorMessages } from '../messages/error-messages';

export interface ApiError {
  message: string;
}

interface ApiErrorResponse {
  message: string;
}

export function toApiError(error: HttpErrorResponse): ApiError {
  const response = parseApiErrorResponse(error.error);

  return { message: response?.message ?? errorMessages.unexpected };
}

function parseApiErrorResponse(value: unknown): ApiErrorResponse | null {
  if (typeof value !== 'object' || value === null || !('message' in value)) {
    return null;
  }

  const message = value.message;
  return typeof message === 'string' ? { message } : null;
}
