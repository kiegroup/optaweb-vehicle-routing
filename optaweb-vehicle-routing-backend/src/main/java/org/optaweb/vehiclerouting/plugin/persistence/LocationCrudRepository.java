package org.optaweb.vehiclerouting.plugin.persistence;

import javax.enterprise.context.ApplicationScoped;

import io.quarkus.hibernate.orm.panache.PanacheRepository;

/**
 * Location repository.
 */
@ApplicationScoped
public class LocationCrudRepository implements PanacheRepository<LocationEntity> {

}
