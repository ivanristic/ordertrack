package rs.biljnaapotekasvstefan.ordertrack.model;
// Generated Jun 14, 2017 10:17:23 PM by Hibernate Tools 5.2.3.Final

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

/**
 * PersistentLogins generated by hbm2java
 */
@Entity
@Table(schema="ord")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PersistentLogins implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8340980993971787034L;
	@Id
	@Column(name = "series", unique = true, nullable = false)
	private String series;

	@OneToOne
	@JoinColumn(name = "username")
	private Users users;

	@Column(name = "token", nullable = false)
	private String token;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "last_used", nullable = false, length = 19)
	private Date lastUsed;

}
