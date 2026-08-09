describe('Home page', () => {
  it('displays the MDD welcome message', () => {
    cy.visit('/');

    cy.contains('h1', 'Welcome to MDD').should('be.visible');
  });
});
