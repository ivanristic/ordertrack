package rs.biljnaapotekasvstefan.ordertrack.repository;

import org.springframework.data.repository.CrudRepository;
import rs.biljnaapotekasvstefan.ordertrack.model.Customers;
import rs.biljnaapotekasvstefan.ordertrack.model.Users;

public interface UsersRepository extends CrudRepository<Users, String> {
    Users findByUsername(String username);
}
