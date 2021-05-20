package es.vn.sb.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	private static final String[] AUTH_WHITE_LIST = {
			// spring actuator
			"/actuator/health",
			};

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		// @formatter:off
		http.httpBasic()
	        .and()
	        .authorizeRequests()
	        .antMatchers(AUTH_WHITE_LIST).permitAll()
			.antMatchers("/actuator/**").hasRole("ENDPOINT_ADMIN")
	        .antMatchers("/hello/**").permitAll()
	        .and()
	        .csrf().disable()
	        .formLogin().disable();
		// @formatter:on
	}

	@Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {

        auth.inMemoryAuthentication()
                .withUser("user").password("{noop}password").roles("USER")
                .and()
                .withUser("monitor").password("{noop}monitor123").roles("ENDPOINT_ADMIN")
                .and()
                .withUser("admin").password("{noop}password").roles("USER", "ADMIN");

    }
}
