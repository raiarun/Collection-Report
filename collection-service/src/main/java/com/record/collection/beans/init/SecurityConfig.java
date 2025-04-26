package com.record.collection.beans.init;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;

import com.record.collection.filter.CustomizedCsrfFilter;
import com.record.collection.reload.ApplicationProperties;

@Configuration
@EnableWebSecurity
@Import({BeanConfig.class})
public class SecurityConfig extends WebSecurityConfigurerAdapter {
	
	@Autowired
    PasswordEncoder passwordEncoder;
	
	@Autowired
    private DataSource dataSource;
	
	@Autowired
	ApplicationProperties _config;

	@Autowired
    public void configAuthentication(AuthenticationManagerBuilder auth) throws Exception {
        auth.jdbcAuthentication().passwordEncoder(new BCryptPasswordEncoder())
            .dataSource(dataSource)
            .usersByUsernameQuery("select username, password, enabled from users where username=?")
            .authoritiesByUsernameQuery("select username, role from users where username=?");
    }
	
	@Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
	
	@Override
    protected void configure(HttpSecurity http) throws Exception {
		http
        .authorizeRequests()
        	.antMatchers("/resources/**", "/js/**", "/css/**").permitAll()
            .antMatchers("/").hasAnyRole("USER", "ADMIN")
            .antMatchers("/save").hasAnyRole("USER", "ADMIN")
            .antMatchers("/get").hasAnyRole("USER", "ADMIN")
            .anyRequest().authenticated()
            .and()
        .formLogin()
            .loginPage("/login")
            .permitAll()
            .and()
        .logout()                                    
            .permitAll();
		http.csrf().csrfTokenRepository(csrfTokenRepository())
            .and()
            .addFilterAfter(new CustomizedCsrfFilter(), CsrfFilter.class);
    }

	private CsrfTokenRepository csrfTokenRepository() {
		HttpSessionCsrfTokenRepository repository = new HttpSessionCsrfTokenRepository();
		repository.setHeaderName(CustomizedCsrfFilter.CSRF_COOKIE_NAME);
		return repository;
	}
	
}
