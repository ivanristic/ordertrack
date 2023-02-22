package rs.biljnaapotekasvstefan.ordertrack.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import rs.biljnaapotekasvstefan.ordertrack.model.Locations;

@Repository
public interface LocationsRepository extends CrudRepository<Locations, Long> {
    Locations findByLocation(String text);
}
