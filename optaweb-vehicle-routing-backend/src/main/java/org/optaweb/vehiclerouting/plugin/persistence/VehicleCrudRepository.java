package org.optaweb.vehiclerouting.plugin.persistence;

import javax.enterprise.context.ApplicationScoped;

import io.quarkus.hibernate.orm.panache.PanacheRepository;

/**
 * Vehicle repository.
 */
@ApplicationScoped
public class VehicleCrudRepository implements PanacheRepository<VehicleEntity> {

}
