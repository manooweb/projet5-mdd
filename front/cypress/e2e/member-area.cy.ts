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

  it('creates an article and returns to the articles page', () => {
    cy.intercept('GET', '/api/topics', { fixture: 'topics.json' }).as('topics');
    cy.intercept('POST', '/api/posts', { statusCode: 201 }).as('createPost');
    cy.intercept('GET', '/api/posts?sort=desc', { body: [] }).as('posts');

    cy.visitProtected('/posts/create');

    cy.wait('@currentUser');
    cy.wait('@topics');
    cy.get('#topicId').select('1');
    cy.get('#title').type('Tester les parcours');
    cy.get('#content').type('Un article créé depuis le formulaire.');
    cy.contains('button', 'Créer').click();

    cy.wait('@createPost').its('request.body').should('deep.equal', {
      topicId: 1,
      title: 'Tester les parcours',
      content: 'Un article créé depuis le formulaire.',
    });
    cy.wait('@currentUser');
    cy.wait('@posts');
    cy.location('pathname').should('eq', '/posts');
  });

  it('displays an article, creates a comment and reloads it', () => {
    let postRequestCount = 0;

    cy.intercept('GET', '/api/posts/12', (request) => {
      postRequestCount += 1;
      request.reply({
        fixture: postRequestCount === 1 ? 'post-detail.json' : 'post-detail-after-comment.json',
      });
    }).as('post');
    cy.intercept('POST', '/api/posts/12/comments', { statusCode: 201 }).as('createComment');

    cy.visitProtected('/posts/12');

    cy.wait('@currentUser');
    cy.wait('@post');
    cy.contains('h1', 'Tester une API').should('be.visible');
    cy.contains('[aria-label="Liste des commentaires"]', 'Un commentaire utile.').should(
      'be.visible',
    );
    cy.get('app-post-comments #content').type('Un second commentaire.');
    cy.get('button[aria-label="Envoyer le commentaire"]').click();

    cy.wait('@createComment').its('request.body').should('deep.equal', {
      content: 'Un second commentaire.',
    });
    cy.wait('@post');
    cy.contains('[aria-label="Liste des commentaires"]', 'Un second commentaire.').should(
      'be.visible',
    );
  });

  it('shows a clear message when an article does not exist', () => {
    cy.intercept('GET', '/api/posts/404', { statusCode: 404 }).as('missingPost');

    cy.visitProtected('/posts/404');

    cy.wait('@currentUser');
    cy.wait('@missingPost');
    cy.get('[role="alert"]').should('contain.text', 'Article introuvable.');
    cy.get('a[aria-label="Retour aux articles"]').should('have.attr', 'href', '/posts');
  });

  it('opens the mobile navigation and closes it with Escape or its backdrop', () => {
    cy.viewport(375, 667);
    cy.intercept('GET', '/api/posts?sort=desc', { body: [] }).as('posts');

    cy.visitProtected('/posts');

    cy.wait('@currentUser');
    cy.wait('@posts');
    cy.get('button[aria-label="Ouvrir le menu de navigation"]').click();
    cy.get('dialog[aria-label="Navigation principale"]').should('have.attr', 'open');
    cy.get('dialog').within(() => {
      cy.get('a[aria-label="Articles"]').should('be.visible');
      cy.get('a[aria-label="Thèmes"]').should('be.visible');
      cy.get('a[aria-label="Mon profil"]').should('be.visible');
    });

    cy.get('dialog').trigger('keydown', { key: 'Escape', code: 'Escape' });
    cy.get('dialog').should('not.exist');

    cy.get('button[aria-label="Ouvrir le menu de navigation"]').click();
    cy.get('dialog').should('have.attr', 'open');
    cy.get('dialog').click('topLeft');
    cy.get('dialog').should('not.exist');
  });

  it('shows the not-found page for an authenticated user on an unknown route', () => {
    cy.visitProtected('/unknown-page');

    cy.wait('@currentUser');
    cy.contains('h1', 'Page introuvable').should('be.visible');
    cy.contains('a', 'Retour aux articles').should('have.attr', 'href', '/posts');
  });

  it('redirects an unauthenticated visitor from a protected route to login', () => {
    cy.intercept('GET', '/api/users/me', { statusCode: 401 }).as('missingSession');

    cy.visit('/posts');

    cy.wait('@missingSession');
    cy.location('pathname').should('eq', '/login');
  });

  it('returns to login when the active session expires while loading articles', () => {
    cy.intercept('GET', '/api/posts?sort=desc', { statusCode: 401 }).as('expiredSession');

    cy.visitProtected('/posts');

    cy.wait('@currentUser');
    cy.wait('@expiredSession');
    cy.location('pathname').should('eq', '/login');
  });

  it('keeps an incomplete article form local', () => {
    cy.intercept('GET', '/api/topics', { fixture: 'topics.json' }).as('topics');
    cy.intercept('POST', '/api/posts').as('createPost');

    cy.visitProtected('/posts/create');

    cy.wait('@currentUser');
    cy.wait('@topics');
    cy.get('form').submit();

    cy.get('@createPost.all').should('have.length', 0);
    cy.location('pathname').should('eq', '/posts/create');
  });

  it('displays the API error and keeps profile saving available after a rejected update', () => {
    cy.intercept('GET', '/api/topics', { fixture: 'topics.json' }).as('topics');
    cy.intercept('PATCH', '/api/users/me', {
      statusCode: 409,
      body: { messageCode: 'DUPLICATE_IDENTITY' },
    }).as('updateProfile');

    cy.visitProtected('/profile');

    cy.wait('@currentUser');
    cy.wait('@topics');
    cy.get('#username').clear().type('manu-updated');
    cy.contains('button', 'Sauvegarder').click();

    cy.wait('@updateProfile');
    cy.get('[role="alert"]').should(
      'have.text',
      'Ce nom d’utilisateur ou cette adresse e-mail est déjà utilisé(e).',
    );
    cy.contains('button', 'Sauvegarder').should('not.be.disabled');
  });

  function interceptTopics(refreshedFixture: string): void {
    let requestCount = 0;

    cy.intercept('GET', '/api/topics', (request) => {
      requestCount += 1;
      request.reply({ fixture: requestCount === 1 ? 'topics.json' : refreshedFixture });
    }).as('topics');
  }
});
