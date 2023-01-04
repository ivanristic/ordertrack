package rs.biljnaapotekasvstefan.ordertrack.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(schema="ord")
@Getter
@Setter
public class Customers {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO, generator="native")
    @GenericGenerator(name = "native", strategy = "native")
    @Column(nullable = false)
    private Long customerId;

    @Column
    private String name;

    @Column
    private String address;

    @Column
    private String city;

    @Column
    private String phone;
    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "username", updatable = false, nullable = false)
    private Users users;

    @OneToMany(mappedBy = "customers")
    private Set<Orders> orders;


}