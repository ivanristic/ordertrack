package rs.biljnaapotekasvstefan.ordertrack.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(schema="ord", name="locations")
@Getter
@Setter
public class Locations {

    @Id
    @Column(unique = true, updatable = false)
    @GeneratedValue(strategy= GenerationType.AUTO, generator="native")
    @GenericGenerator(name = "native", strategy = "native")
    private Long locationId;

    @Column
    private String location;
}
