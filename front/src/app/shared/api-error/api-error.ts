import { HttpErrorResponse } from '@angular/common/http';
import { errorMessages } from '../messages/error-messages';

export interface ApiError {
  message: string;
}

interface ApiErrorResponse {
  messageCode: string;
}

export function toApiError(error: HttpErrorResponse): ApiError {
  const response = parseApiErrorResponse(error.error);

  return {
    message: response === null ? errorMessages.unexpected : messageFor(response.messageCode),
  };
}

function parseApiErrorResponse(value: unknown): ApiErrorResponse | null {
  if (typeof value !== 'object' || value === null || !('messageCode' in value)) {
    return null;
  }

  const messageCode = value.messageCode;
  return typeof messageCode === 'string' ? { messageCode } : null;
}

function messageFor(messageCode: string): string {
  return isKnownMessageCode(messageCode) ? errorMessages[messageCode] : errorMessages.unexpected;
}

function isKnownMessageCode(messageCode: string): messageCode is keyof typeof errorMessages {
  return messageCode in errorMessages && messageCode !== 'unexpected';
}
