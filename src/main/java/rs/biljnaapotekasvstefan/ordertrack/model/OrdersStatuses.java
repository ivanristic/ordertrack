package rs.biljnaapotekasvstefan.ordertrack.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(schema="ord", name="orders_statuses")
@Getter
@Setter
@ToString
public class OrdersStatuses {

    @EmbeddedId
    private OrdersStatusId ordersStatusId;

    @Column
    private LocalDateTime statusTime;

    @Column
    private LocalDateTime localStatusTime;

    @Column
    private String regionalCenterPhone;

    @ManyToOne
    @JoinColumn(name = "statusId", insertable = false, updatable = false)
    private Statuses statuses;
    @ManyToOne
    @JoinColumn(name = "orderId", insertable = false, updatable = false)
    private Orders orders;
    @ManyToOne
    @JoinColumn(name = "locationId", insertable = false, updatable = false)
    private Locations locations;

}