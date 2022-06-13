package org.optaweb.vehiclerouting.plugin.persistence;

import static java.util.stream.Collectors.toList;

import java.util.List;
import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.optaweb.vehiclerouting.domain.Coordinates;
import org.optaweb.vehiclerouting.domain.Location;
import org.optaweb.vehiclerouting.service.location.LocationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
class LocationRepositoryImpl implements LocationRepository {

    private static final Logger logger = LoggerFactory.getLogger(LocationRepositoryImpl.class);
    private final LocationCrudRepository repository;

    @Inject
    LocationRepositoryImpl(LocationCrudRepository repository) {
        this.repository = repository;
    }

    @Override
    public Location createLocation(Coordinates coordinates, String description) {
        LocationEntity locationEntity = new LocationEntity(0, coordinates.latitude(), coordinates.longitude(), description);
        repository.persist(locationEntity);
        Location location = toDomain(locationEntity);
        logger.info("Created location {}.", location.fullDescription());
        return location;
    }

    @Override
    public List<Location> locations() {
        return repository.streamAll()
                .map(LocationRepositoryImpl::toDomain)
                .collect(toList());
    }

    @Override
    public Location removeLocation(long id) {
        Optional<LocationEntity> maybeLocation = repository.findByIdOptional(id);
        maybeLocation.ifPresent(locationEntity -> repository.deleteById(id));
        LocationEntity locationEntity = maybeLocation.orElseThrow(
                () -> new IllegalArgumentException("Location{id=" + id + "} doesn't exist"));
        Location location = toDomain(locationEntity);
        logger.info("Deleted location {}.", location.fullDescription());
        return location;
    }

    @Override
    public void removeAll() {
        repository.deleteAll();
    }

    @Override
    public Optional<Location> find(long locationId) {
        return repository.findByIdOptional(locationId).map(LocationRepositoryImpl::toDomain);
    }

    private static Location toDomain(LocationEntity locationEntity) {
        return new Location(
                locationEntity.getId(),
                new Coordinates(locationEntity.getLatitude(), locationEntity.getLongitude()),
                locationEntity.getDescription());
    }
}
