describe('Authentication', () => {
  const credentials = {
    login: 'manu@example.com',
    password: 'Pass1!wd',
  };

  const registrationDetails = {
    username: 'manu',
    email: 'manu@example.com',
    password: 'Pass1!wd',
  };

  beforeEach(() => {
    cy.intercept('GET', '/api/auth/csrf', { statusCode: 204 }).as('csrfToken');
  });

  it('logs in with valid credentials and redirects to the articles page', () => {
    interceptAuthenticatedArticlesPage();
    cy.intercept('POST', '/api/auth/login', { statusCode: 204 }).as('login');

    cy.visit('/login');
    cy.get('#login').type(credentials.login);
    cy.get('#password').type(credentials.password);
    cy.contains('button', 'Se connecter').click();

    cy.wait('@csrfToken');
    cy.wait('@login').its('request.body').should('deep.equal', credentials);
    cy.wait('@currentUser');
    cy.wait('@posts');
    cy.location('pathname').should('eq', '/posts');
  });

  it('displays the API error when login is rejected', () => {
    cy.intercept('POST', '/api/auth/login', {
      statusCode: 401,
      body: { messageCode: 'INVALID_CREDENTIALS' },
    }).as('login');

    cy.visit('/login');
    cy.get('#login').type(credentials.login);
    cy.get('#password').type(credentials.password);
    cy.contains('button', 'Se connecter').click();

    cy.wait('@csrfToken');
    cy.wait('@login').its('request.body').should('deep.equal', credentials);
    cy.get('[role="alert"]').should('have.text', 'Identifiants incorrects.');
    cy.location('pathname').should('eq', '/login');
  });

  it('registers a user and redirects to the articles page', () => {
    interceptAuthenticatedArticlesPage();
    cy.intercept('POST', '/api/auth/register', { statusCode: 204 }).as('register');

    cy.visit('/register');
    cy.get('#username').type(registrationDetails.username);
    cy.get('#email').type(registrationDetails.email);
    cy.get('#password').type(registrationDetails.password).blur();
    cy.contains('button', 'S’inscrire').click();

    cy.wait('@csrfToken');
    cy.wait('@register').its('request.body').should('deep.equal', registrationDetails);
    cy.wait('@currentUser');
    cy.wait('@posts');
    cy.location('pathname').should('eq', '/posts');
  });

  it('displays the API error when registration is rejected', () => {
    cy.intercept('POST', '/api/auth/register', {
      statusCode: 409,
      body: { messageCode: 'DUPLICATE_IDENTITY' },
    }).as('register');

    cy.visit('/register');
    cy.get('#username').type(registrationDetails.username);
    cy.get('#email').type(registrationDetails.email);
    cy.get('#password').type(registrationDetails.password).blur();
    cy.contains('button', 'S’inscrire').click();

    cy.wait('@csrfToken');
    cy.wait('@register').its('request.body').should('deep.equal', registrationDetails);
    cy.get('[role="alert"]').should(
      'have.text',
      'Ce nom d’utilisateur ou cette adresse e-mail est déjà utilisé(e).',
    );
    cy.location('pathname').should('eq', '/register');
  });

  function interceptAuthenticatedArticlesPage(): void {
    cy.intercept('GET', '/api/users/me', {
      body: { id: 1, username: 'manu', email: 'manu@example.com' },
    }).as('currentUser');
    cy.intercept('GET', '/api/posts?sort=desc', { body: [] }).as('posts');
  }
});
