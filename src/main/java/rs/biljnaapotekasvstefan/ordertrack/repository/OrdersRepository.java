package rs.biljnaapotekasvstefan.ordertrack.repository;

import org.springframework.data.repository.CrudRepository;
import rs.biljnaapotekasvstefan.ordertrack.model.Orders;

import java.time.LocalDateTime;
import java.util.List;

public interface OrdersRepository extends CrudRepository<Orders, Long> {
    Orders findOrdersByOrderId(String orderNumber);
    //List<Orders> findOrdersByStatus(Integer status);
    //List<Orders> findOrderByStatusNotAndCustomersUsersUsername(Integer status, String username);

    List<Orders> findOrderByOrdersStatusesStatusesDelivered(Boolean status);
    //List<Orders> findOrdersByStatusAndStatusDateAfter(Boolean status, LocalDateTime dateTime);

}
