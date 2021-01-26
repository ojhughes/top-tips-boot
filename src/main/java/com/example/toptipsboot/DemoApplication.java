package com.example.toptipsboot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfiguration;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.security.config.Customizer.withDefaults;

@SpringBootApplication
@RestController
@EnableConfigurationProperties(DemoApplication.UserProperties.class)
@EnableWebSecurity
public class DemoApplication extends WebSecurityConfigurerAdapter {

	@GetMapping("/demo")
	public String demo() {
		return "k8s is easy!";
	}

	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	UserDetailsService userDetailsService(PasswordEncoder passwordEncoder,
										  UserProperties userProperties) {
		UserDetails user = User.withUsername(userProperties.getUsername())
							   .password(passwordEncoder.encode(userProperties.getPassword()))
							   .authorities("ROLE_USER")
							   .build();
		return new InMemoryUserDetailsManager(user);

	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
				.authorizeRequests()
				.antMatchers("/actuator/**").anonymous()
				.anyRequest().authenticated()
				.and()
				.httpBasic(withDefaults());

	}

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

	@ConfigurationProperties("toptips.security")
	public static class UserProperties {

		private String username;
		private String password;

		public String getUsername() {
			return username;
		}

		public void setUsername(String username) {
			this.username = username;
		}

		public String getPassword() {
			return password;
		}

		public void setPassword(String password) {
			this.password = password;
		}
	}
}
