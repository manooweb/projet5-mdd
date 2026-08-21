import { ApplicationConfig, provideBrowserGlobalErrorListeners } from '@angular/core';
import { provideHttpClient, withInterceptors, withXsrfConfiguration } from '@angular/common/http';
import { provideRouter } from '@angular/router';
import { providePrimeNG } from 'primeng/config';

import { routes } from './app.routes';
import { sessionExpirationInterceptor } from './auth/session-expiration.interceptor';
import { MddPreset } from './theme/mdd-preset';
import { primeUiLicense } from './config/primeui-license.local';

export const appConfig: ApplicationConfig = {
  providers: [
    provideBrowserGlobalErrorListeners(),
    provideHttpClient(
      withInterceptors([sessionExpirationInterceptor]),
      withXsrfConfiguration({
        cookieName: 'XSRF-TOKEN',
        headerName: 'X-XSRF-TOKEN',
      }),
    ),
    provideRouter(routes),
    providePrimeNG({
      license: primeUiLicense,
      theme: {
        preset: MddPreset,
        options: {
          cssLayer: {
            name: 'primeng',
            order: 'theme, base, primeng',
          },
          darkModeSelector: false,
        },
      },
    }),
  ],
};
