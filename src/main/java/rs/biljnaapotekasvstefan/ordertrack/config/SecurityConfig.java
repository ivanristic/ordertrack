package rs.biljnaapotekasvstefan.ordertrack.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;

import javax.naming.NamingException;
import javax.sql.DataSource;

@Configuration
public class SecurityConfig {

	@Autowired
	DataSource dataSource;


	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http.authorizeRequests()
				.anyRequest().access("hasAuthority('admin')")
				.and().formLogin().permitAll()
				//.and().cors()
				//.and().csrf().disable()
				;
				//.and().rememberMe()

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
	public AuthenticationManager authManager(HttpSecurity http, BCryptPasswordEncoder bCryptPasswordEncoder)  throws Exception {
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
}
