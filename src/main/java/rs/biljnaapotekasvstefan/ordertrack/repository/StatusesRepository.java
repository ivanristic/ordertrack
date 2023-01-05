package rs.biljnaapotekasvstefan.ordertrack.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import rs.biljnaapotekasvstefan.ordertrack.model.Statuses;

@Repository
public interface StatusesRepository extends CrudRepository<Statuses, Long> {

    Statuses findByStatus(String status);
}
