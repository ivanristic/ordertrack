package rs.biljnaapotekasvstefan.ordertrack.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import rs.biljnaapotekasvstefan.ordertrack.model.Emails;

import java.util.List;

@Repository
public interface EmailsRepository extends CrudRepository<Emails, Long> {

}
