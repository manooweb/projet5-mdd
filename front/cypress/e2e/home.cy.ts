describe('Home page', () => {
  it('displays the MDD logo and the authentication links', () => {
    cy.visit('/');

    cy.get('main').within(() => {
      cy.get('img[alt="MDD logo"]').should('be.visible');
      cy.contains('a', 'Se connecter').should('have.attr', 'href', '/login');
      cy.contains('a', 'S’inscrire').should('have.attr', 'href', '/register');
    });
  });
});
