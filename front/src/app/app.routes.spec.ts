import { routes } from './app.routes';

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
});
