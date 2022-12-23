package rs.biljnaapotekasvstefan.ordertrack.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import rs.biljnaapotekasvstefan.ordertrack.model.Email;

@Repository
public interface EmailRepository extends CrudRepository<Email, Long> {

}
