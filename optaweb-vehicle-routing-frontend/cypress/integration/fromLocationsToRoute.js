describe('Locations can be added and route is computed', () => {
  const cities = ['Garz', 'Hoppenrade'];

  /**
   * Adds a location by searching for a city of a given name.
   * @param { string } name - city name
   */
  const addCity = (name) => {
    cy.get('[data-cy=geosearch-text-input]').type(name);
    // TODO: replace by mocking the request (search depends on a 3rd-party service)
    cy.get('[data-cy=geosearch-location-item-button-0]', { timeout: 60000 }).click();
  };

  /**
   * Clears locations by clicking on the 'Clear' button.
   */
  const clearLocations = () => {
    // Add one city to make sure there is a location in the list and the clear button shows up
    addCity('Garz');
    cy.get('[data-cy=demo-clear-button]').click({ force: true });
    cy.wait('@postClear');
  };

  /**
   * Waits for a websocket connection to be established.
   */
  const visitDemo = () => {
    cy.visit('/');
    cy.get('a[href="/demo"]').click();
  };

  before(() => {
    cy.intercept('POST', '**/api/clear').as('postClear');
    cities.forEach((city) => cy.intercept(
      'GET',
      `https://nominatim.openstreetmap.org/search?*q=${city}`,
      { fixture: `response-${city.toLowerCase()}.json` },
    ));
    visitDemo();
    clearLocations();
  });

  it('Locations added via clicking on a map are added to a route', () => {
    cities.forEach((city) => {
      addCity(city);
    });

    cy.get('[data-cy=demo-add-vehicle]').click();
    cy.get('a[href="/routes"]').click();
    cy.get('[data-cy=location-list]').find('li').should((list) => {
      cities.forEach((city) => expect(list).to.contain(city));
    });
  });
});
