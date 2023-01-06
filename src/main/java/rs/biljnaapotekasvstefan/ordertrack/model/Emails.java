package rs.biljnaapotekasvstefan.ordertrack.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import jakarta.persistence.*;

@Entity
@Table(schema="ord")
@Getter
@Setter
public class Emails {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO, generator="native")
    @GenericGenerator(name = "native", strategy = "native")
    @Column(name = "id", nullable = false)
    private Long id;

    @Column
    private String email;

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "username", updatable = false, nullable = false)
    private Users users;

}
