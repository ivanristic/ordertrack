package rs.biljnaapotekasvstefan.ordertrack.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import jakarta.persistence.*;
import java.util.Set;

@Entity
@Table(schema="ord", name="statuses")
@Getter
@Setter
public class Statuses {

    @Id
    @Column(unique = true, updatable = false)
    private Long statusId;

    @Column
    private String status;

    @Column
    private Boolean track;

    @Column
    private Boolean delivered;

    @Column
    private Long timeDelay;

    @OneToMany(mappedBy = "statuses")
    private Set<OrdersStatuses> ordersStatuses;
}
