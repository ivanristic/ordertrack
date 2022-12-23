package rs.biljnaapotekasvstefan.ordertrack.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import rs.biljnaapotekasvstefan.ordertrack.model.OrdersStatuses;

import java.util.*;

public interface OrdersStatusesRepository extends CrudRepository<OrdersStatuses, Long> {

    @Query("select os from OrdersStatuses os, Orders o, Customers c " +
            "where os.ordersStatusId.orderId = o.orderId " +
            "and o.status != 1 " +
            "and o.customers = c.customerId " +
            "and c.users.username = :username " +
            "and os.statusTime = (select MAX(os2.statusTime) from OrdersStatuses os2 " +
                "where os2.ordersStatusId.orderId = os.ordersStatusId.orderId)")
    List<OrdersStatuses> findUndeliveredOrderStatusForActiveUser(@Param("username") String username);

}
