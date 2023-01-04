package rs.biljnaapotekasvstefan.ordertrack.model;
// Generated Jun 14, 2017 10:17:23 PM by Hibernate Tools 5.2.3.Final

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;
import java.util.*;

/**
 * Users generated by hbm2java
 */
@Entity
@Table(schema="ord")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Users  implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4963210084028783990L;
	@Id
	@Column(name = "username", unique = true, nullable = false, length = 50)
	private String username;

	@Column(name = "password", nullable = false)
	private String password;

	@Column(name = "enabled", nullable = false)
	private boolean enabled;

	@JsonManagedReference
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "users")
	private Set<Authorities> authorities;

	@JsonManagedReference
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "users")
	private List<Customers> customers;

	@JsonManagedReference
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "users")
	private List<Emails> emails;


}
