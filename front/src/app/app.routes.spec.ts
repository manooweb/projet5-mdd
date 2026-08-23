import { routes } from './app.routes';
import { unknownRouteGuard } from './auth/unknown-route.guard';
import { NotFoundComponent } from './not-found/not-found.component';

describe('application routes', () => {
  it('exposes protected destinations for articles, article detail, article creation, topics, and profile', () => {
    expect(routes.map((route) => route.path)).toEqual(
      expect.arrayContaining(['posts', 'posts/:postId', 'posts/create', 'topics', 'profile']),
    );
    expect(routes.find((route) => route.path === 'posts')?.canActivate).toHaveLength(1);
    expect(routes.find((route) => route.path === 'posts/:postId')?.canActivate).toHaveLength(1);
    expect(routes.find((route) => route.path === 'posts/create')?.canActivate).toHaveLength(1);
    expect(routes.find((route) => route.path === 'topics')?.canActivate).toHaveLength(1);
    expect(routes.find((route) => route.path === 'profile')?.canActivate).toHaveLength(1);
  });

  it('keeps authenticated users in the application for an unknown route', () => {
    const notFoundRoute = routes.find((route) => route.path === '**');

    expect(notFoundRoute?.component).toBe(NotFoundComponent);
    expect(notFoundRoute?.canActivate).toEqual([unknownRouteGuard]);
  });
});
