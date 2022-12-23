package rs.biljnaapotekasvstefan.ordertrack.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(schema="ord", uniqueConstraints = @UniqueConstraint(columnNames = { "currentStatus", "orderId" }))
@Getter
@Setter
@ToString
public class OrdersStatuses {

    @EmbeddedId
    private OrdersStatusId ordersStatusId;

    @Column
    private LocalDateTime statusTime;

    @Column
    private String location;

    @Column
    private String regionalCenterPhone;

    @ManyToOne
    @JoinColumn(name = "orderId", nullable = false, insertable = false, updatable = false)
    private Orders orders;

}