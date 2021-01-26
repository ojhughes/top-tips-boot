Download project starter
`spring init --build=gradle --dependencies=web,actuator top-tips-boot-k8s`

* Git commit for skaffold

Add rest controller

```java
@SpringBootApplication
@RestController
public class DemoApplication {

	@GetMapping("/demo")
	public String demo(){
		return "k8s is easy!";
	}

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

}
```

* Create k8s directory

* Create deployment.yaml

* Auto complete kdep..

* Add schema mapping https://kubernetesjsonschema.dev/v1.18.1/deployment-apps-v1.json

* Add liveness and readiness probes

* Set resources
    * Requests equal to limits for memory
    * No limit for CPU

* Create Service
    * LoadBalancer

* Create Namespace

* Create Kustomize manifest
```bash
kustomize create \
--autodetect \
--namespace top-tips /
--labels="app.kubernetes.io/version:0.0.1,app.kubernetes.io/framework:spring-boot-2.4"
```
* Add Kubernetes support
    * Change Buildpacks builder `gcr.io/paketo-buildpacks/builder:base`
    
* `skaffold dev` in terminal

* `curl 192.168.1.227:8080/demo`

* Exit skaffold and debug

* Add application.yaml and deployment volumes

```yaml
        volumeMounts:
          - mountPath: /workspace/config/top-tips
            name: spring-application-config-volume
      volumes:
        - name: spring-application-config-volume
          configMap:
            name: spring-application-config
```

* Add basic security
```java
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
	UserDetailsService userDetailsService(PasswordEncoder passwordEncoder, UserProperties userProperties) {
		final UserDetails user = User.withUsername(userProperties.getUsername())
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
```
