package rs.biljnaapotekasvstefan.ordertrack.model;

import lombok.*;

import jakarta.persistence.*;
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
    private Long statusId;

    @Column
    private String orderId;

}