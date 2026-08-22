import { HttpErrorResponse } from '@angular/common/http';
import { toApiError } from './api-error';

describe('toApiError', () => {
  it('maps a known API message code to its French message', () => {
    const error = new HttpErrorResponse({
      status: 401,
      error: {
        messageCode: 'INVALID_CREDENTIALS',
        message: 'Invalid credentials.',
      },
    });

    expect(toApiError(error)).toEqual({ message: 'Identifiants incorrects.' });
  });

  it('uses the fallback message for an unknown API message code', () => {
    const error = new HttpErrorResponse({
      status: 500,
      error: {
        messageCode: 'FUTURE_ERROR',
        message: 'A future error message.',
      },
    });

    expect(toApiError(error)).toEqual({
      message: 'Une erreur inattendue est survenue. Veuillez réessayer.',
    });
  });
});
