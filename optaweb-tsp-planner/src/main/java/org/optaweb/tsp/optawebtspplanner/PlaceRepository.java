package org.optaweb.tsp.optawebtspplanner;

import org.springframework.data.repository.CrudRepository;
import org.springframework.web.bind.annotation.CrossOrigin;

@CrossOrigin
public interface PlaceRepository extends CrudRepository<Place, Long> {

}
