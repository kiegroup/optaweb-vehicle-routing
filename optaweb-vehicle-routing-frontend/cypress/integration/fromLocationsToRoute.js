/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

describe('Locations can be added and route is computed', () => {
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
  };

  /**
   * Waits for a websocket connection to be established.
   */
  const visitDemo = () => {
    cy.visit('/');
    cy.get('a[href="/demo"]').click();
  };

  before(() => {
    visitDemo();
    clearLocations();
  });

  it('Locations added via clicking on a map are added to a route', () => {
    const cities = ['Garz', 'Hoppenrade'];

    cities.forEach((city) => {
      addCity(city);
    });

    cy.get('[data-cy=demo-add-vehicle]').click();
    cy.get('a[href="/route"]').click();
    cy.get('[data-cy=location-list]').find('li').should((list) => {
      cities.forEach(city => expect(list).to.contain(city));
    });
  });
});
