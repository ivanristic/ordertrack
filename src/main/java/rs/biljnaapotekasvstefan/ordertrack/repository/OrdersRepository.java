package rs.biljnaapotekasvstefan.ordertrack.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import rs.biljnaapotekasvstefan.ordertrack.model.Orders;

import java.time.LocalDateTime;
import java.util.List;

public interface OrdersRepository extends CrudRepository<Orders, Long> {
    Orders findOrdersByOrderId(String orderNumber);
    //List<Orders> findOrdersByStatus(Integer status);
    //List<Orders> findOrderByStatusNotAndCustomersUsersUsername(Integer status, String username);

    List<Orders> findOrderByOrdersStatusesStatusesDelivered(Boolean status);

    @Query("select o from OrdersStatuses os " +
            "left join Statuses s on (os.ordersStatusId.statusId = s.statusId and s.delivered = 0) " +
            "left join Orders o on os.ordersStatusId.orderId = o.orderId " +
            "inner join OrdersStatuses os2 on " +
            "os2.statusTime = (select MAX(os2.statusTime) from OrdersStatuses os2 where os2.ordersStatusId.orderId = os.ordersStatusId.orderId) " +
            "and os2.ordersStatusId.orderId = os.ordersStatusId.orderId")
    List<Orders> findUndeliveredOrders();
    //List<Orders> findOrdersByStatusAndStatusDateAfter(Boolean status, LocalDateTime dateTime);

}
