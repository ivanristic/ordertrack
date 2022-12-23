package rs.biljnaapotekasvstefan.ordertrack.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import rs.biljnaapotekasvstefan.ordertrack.model.Customers;

@Repository
public interface CustomerRepository extends CrudRepository<Customers, Long> {
    Customers findCustomerByPhone(String phone);

}
