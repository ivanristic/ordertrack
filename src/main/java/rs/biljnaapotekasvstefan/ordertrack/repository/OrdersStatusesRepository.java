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
    @Query("select os from OrdersStatuses os, Statuses s, Orders o " +
            "where os.localStatusTime = " +
                "(select max(os2.localStatusTime) from OrdersStatuses os2 " +
                "where os2.ordersStatusId.orderId = os.ordersStatusId.orderId) " +
            "and (ifnull(os.ordersStatusId.statusId, 9) = s.statusId and s.delivered=0) " +
            "and os.ordersStatusId.orderId = o.orderId " +
            "and o.users.username = :username"
    )
    List<OrdersStatuses> findUndeliveredOrdersOrderStatusesForUser(@Param("username") String username);
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
