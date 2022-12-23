package rs.biljnaapotekasvstefan.ordertrack.model;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;

@Embeddable
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
public class OrdersStatusId implements Serializable {

    private static final long serialVersionUID = -7558640877458478428L;
    @Column
    private String currentStatus;

    @Column
    private String orderId;

}