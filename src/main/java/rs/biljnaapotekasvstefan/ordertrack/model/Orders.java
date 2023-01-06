package rs.biljnaapotekasvstefan.ordertrack.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(schema="ord", name="orders")
@Getter
@Setter
public class Orders {

    @Id
    @Column(unique = true, updatable = false)
    private String orderId;

    @Column(updatable = false, nullable = false)
    private String shipmentNumber;

    @Column
    private LocalDateTime orderSent;

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "username", updatable = false, nullable = false)
    private Users users;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "customerId")
    private Customers customers;

    @OneToMany(mappedBy = "orders", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Set<OrdersStatuses> ordersStatuses;

}