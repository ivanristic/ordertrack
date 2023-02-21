package rs.biljnaapotekasvstefan.ordertrack.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import rs.biljnaapotekasvstefan.ordertrack.model.OrdersStatuses;

import java.util.*;

public interface OrdersStatusesRepository extends CrudRepository<OrdersStatuses, Long> {

    /*@Query("select os from OrdersStatuses os, Orders o, Statuses s " +
            "where os.ordersStatusId.orderId = o.orderId " +
            "and os.ordersStatusId.statusId = s.statusId " +
            "and s.delivered = 0" +
            "and o.users.username = :username " +
            "and os.statusTime = (select MAX(os2.statusTime) from OrdersStatuses os2 " +
                "where os2.ordersStatusId.orderId = os.ordersStatusId.orderId)")*/
    @Query("select os from OrdersStatuses os " +
            "left join Statuses s on (os.ordersStatusId.statusId = s.statusId and s.delivered = 0) " +
            "left join Orders o on (os.ordersStatusId.orderId = o.orderId and o.users.username = :username) " +
            "inner join OrdersStatuses os2 on " +
            "os2.statusTime = (select MAX(os2.statusTime) from OrdersStatuses os2 where os2.ordersStatusId.orderId = os.ordersStatusId.orderId) " +
            "and os2.ordersStatusId.orderId = os.ordersStatusId.orderId")
    List<OrdersStatuses> findUndeliveredOrdersForUser(@Param("username") String username);
    /*
    @Query("select os from OrdersStatuses os, Orders o, Customers c, Statuses s " +
            "where os.ordersStatusId.orderId = o.orderId " +
            "and os.statusId = s.statusId " +
            "and s.delivered = 0" +
            "and o.customers = c.customerId " +
            "and os.statusTime = (select MAX(os2.statusTime) from OrdersStatuses os2 " +
            "where os2.ordersStatusId.orderId = os.ordersStatusId.orderId)")
    List<OrdersStatuses> findUndeliveredOrders();

*/
}
