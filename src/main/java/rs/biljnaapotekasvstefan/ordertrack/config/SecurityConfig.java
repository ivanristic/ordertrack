package rs.biljnaapotekasvstefan.ordertrack.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import rs.biljnaapotekasvstefan.ordertrack.model.Users;
import rs.biljnaapotekasvstefan.ordertrack.repository.UsersRepository;

import javax.naming.NamingException;
import javax.sql.DataSource;

import java.util.Optional;
import java.util.stream.Collectors;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
public class SecurityConfig {

	@Autowired
	DataSource dataSource;

	@Autowired
	private UsersRepository usersRepository;

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http.authorizeRequests()
				.anyRequest().access("hasAuthority('admin')")
				.and().formLogin(withDefaults())
				//.and().cors()
				//.and().csrf().disable()

				.rememberMe(withDefaults())
				.userDetailsService(userDetailsService())

				//.failureUrl("/login?error")

				//.usernameParameter("username")
				//.passwordParameter("password")
				//.and().logout().logoutSuccessUrl("/login?logout")
				//.anyRequest().permitAll()

				//.and().httpBasic()
		;

		return http.build();
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		PasswordEncoder encoder = new BCryptPasswordEncoder();
		return encoder;
	}

	@Bean
	public AuthenticationManager authManager(HttpSecurity http)  throws Exception {
		//System.out.println(bCryptPasswordEncoder.encode("nirvana"));
		return http.getSharedObject(AuthenticationManagerBuilder.class)
				//.userDetailsService(userDetailsService)
				.jdbcAuthentication()
				.dataSource(dataSource)
				.passwordEncoder(passwordEncoder())
				.and()
				.build();
	}

	@Bean
	public PersistentTokenRepository persistentTokenRepository() {
		JdbcTokenRepositoryImpl tokenRepository = new JdbcTokenRepositoryImpl();
		tokenRepository.setDataSource(dataSource);
		return tokenRepository;
	}
	@Bean
	UserDetailsService userDetailsService(){
		return new UserDetailsService() {
			@Override
			public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
				Optional<Users> opt = Optional.ofNullable(usersRepository.findByUsername(username));
				if(opt.isEmpty())
					throw new UsernameNotFoundException("User with email: " +username +" not found !");
				else {
					Users user = opt.get();
					return new org.springframework.security.core.userdetails.User(
							user.getUsername(),
							user.getPassword(),
							user.getAuthorities()
									.stream()
									.map(role-> new SimpleGrantedAuthority(role.getId().getAuthority()))
									.collect(Collectors.toSet())
					);
				}

			}
		};
	}

}
