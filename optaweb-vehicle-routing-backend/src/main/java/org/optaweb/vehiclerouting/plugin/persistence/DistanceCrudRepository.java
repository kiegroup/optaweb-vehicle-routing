package org.optaweb.vehiclerouting.plugin.persistence;

import javax.enterprise.context.ApplicationScoped;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import io.quarkus.panache.common.Parameters;

/**
 * Distance repository.
 */
@ApplicationScoped
public class DistanceCrudRepository implements PanacheRepositoryBase<DistanceEntity, DistanceKey> {

    void deleteByFromIdOrToId(long deletedLocationId) {
        delete(
                "fromId = :deletedLocationId or toId = :deletedLocationId",
                Parameters.with("deletedLocationId", deletedLocationId));
    }
}
