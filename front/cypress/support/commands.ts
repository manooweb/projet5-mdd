declare global {
  namespace Cypress {
    interface Chainable {
      interceptCsrfToken(): Chainable<void>;
      interceptCurrentUser(): Chainable<void>;
      visitProtected(path: string): Chainable<void>;
    }
  }
}

Cypress.Commands.add('interceptCsrfToken', () => {
  cy.intercept('GET', '/api/auth/csrf', { statusCode: 204 }).as('csrfToken');
});

Cypress.Commands.add('interceptCurrentUser', () => {
  cy.fixture('current-user.json').then((currentUser) => {
    cy.intercept('GET', '/api/users/me', { body: currentUser }).as('currentUser');
  });
});

Cypress.Commands.add('visitProtected', (path: string) => {
  cy.interceptCurrentUser();
  cy.visit(path);
});

export {};
