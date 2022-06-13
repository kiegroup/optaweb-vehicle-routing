package org.optaweb.vehiclerouting.plugin.persistence;

import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.optaweb.vehiclerouting.domain.Distance;
import org.optaweb.vehiclerouting.domain.Location;
import org.optaweb.vehiclerouting.service.distance.DistanceRepository;

@ApplicationScoped
class DistanceRepositoryImpl implements DistanceRepository {

    private final DistanceCrudRepository distanceRepository;

    @Inject
    DistanceRepositoryImpl(DistanceCrudRepository distanceRepository) {
        this.distanceRepository = distanceRepository;
    }

    @Override
    public void saveDistance(Location from, Location to, Distance distance) {
        DistanceEntity distanceEntity = new DistanceEntity(new DistanceKey(from.id(), to.id()), distance.millis());
        distanceRepository.persist(distanceEntity);
    }

    @Override
    public Optional<Distance> getDistance(Location from, Location to) {
        return distanceRepository.findByIdOptional(new DistanceKey(from.id(), to.id()))
                .map(DistanceEntity::getDistance)
                .map(Distance::ofMillis);
    }

    @Override
    public void deleteDistances(Location location) {
        distanceRepository.deleteByFromIdOrToId(location.id());
    }

    @Override
    public void deleteAll() {
        distanceRepository.deleteAll();
    }
}
