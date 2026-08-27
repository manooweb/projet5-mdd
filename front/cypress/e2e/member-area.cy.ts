describe('Member area', () => {
  it('displays articles and reloads them in ascending date order', () => {
    cy.intercept('GET', '/api/posts?sort=desc', { fixture: 'posts-desc.json' }).as(
      'descendingPosts',
    );
    cy.intercept('GET', '/api/posts?sort=asc', { fixture: 'posts-asc.json' }).as('ascendingPosts');

    cy.visitProtected('/posts');

    cy.wait('@currentUser');
    cy.wait('@descendingPosts');
    cy.get('a[aria-label="Lire l’article Article le plus récent"]').should('be.visible');
    cy.get('button[aria-label="Trier par date, ordre décroissant"]').click();

    cy.wait('@ascendingPosts');
    cy.get('button[aria-label="Trier par date, ordre croissant"]').should('be.visible');
    cy.get('a[aria-label^="Lire l’article"]').first().should('contain.text', 'Article plus ancien');
  });

  it('subscribes to a topic and refreshes its state', () => {
    interceptTopics('topics-after-subscription.json');
    cy.intercept('POST', '/api/topics/1/subscription', { statusCode: 204 }).as('subscribe');

    cy.visitProtected('/topics');

    cy.wait('@currentUser');
    cy.wait('@topics');
    cy.contains('article', 'Angular').within(() => cy.contains('button', "S'abonner").click());

    cy.wait('@subscribe');
    cy.wait('@topics');
    cy.contains('article', 'Angular').contains('button', 'Déjà abonné').should('be.disabled');
  });

  it('saves updated profile information', () => {
    cy.intercept('GET', '/api/topics', { fixture: 'topics.json' }).as('topics');
    cy.intercept('PATCH', '/api/users/me', { statusCode: 204 }).as('updateProfile');

    cy.visitProtected('/profile');

    cy.wait('@currentUser');
    cy.wait('@topics');
    cy.get('#username').clear().type('manu-updated');
    cy.contains('button', 'Sauvegarder').click();

    cy.wait('@updateProfile').its('request.body').should('deep.equal', {
      username: 'manu-updated',
      email: 'manu@example.com',
      password: '',
    });
    cy.get('#username').should('have.value', 'manu-updated');
    cy.contains('button', 'Sauvegarder').should('be.disabled');
  });

  it('unsubscribes from a topic and refreshes the profile subscriptions', () => {
    interceptTopics('topics-after-unsubscription.json');
    cy.intercept('DELETE', '/api/topics/2/subscription', { statusCode: 204 }).as('unsubscribe');

    cy.visitProtected('/profile');

    cy.wait('@currentUser');
    cy.wait('@topics');
    cy.contains('article', 'Spring').within(() => cy.contains('button', 'Se désabonner').click());

    cy.wait('@unsubscribe');
    cy.wait('@topics');
    cy.contains('h2', 'Abonnements').parent().should('not.contain.text', 'Spring');
  });

  it('logs out and redirects to the login page', () => {
    cy.intercept('GET', '/api/posts?sort=desc', { body: [] }).as('posts');
    cy.interceptCsrfToken();
    cy.intercept('POST', '/api/auth/logout', { statusCode: 204 }).as('logout');

    cy.visitProtected('/posts');

    cy.wait('@currentUser');
    cy.wait('@posts');
    cy.get('button[aria-label="Se déconnecter"]').click();

    cy.wait('@csrfToken');
    cy.wait('@logout');
    cy.location('pathname').should('eq', '/login');
  });

  function interceptTopics(refreshedFixture: string): void {
    let requestCount = 0;

    cy.intercept('GET', '/api/topics', (request) => {
      requestCount += 1;
      request.reply({ fixture: requestCount === 1 ? 'topics.json' : refreshedFixture });
    }).as('topics');
  }
});
