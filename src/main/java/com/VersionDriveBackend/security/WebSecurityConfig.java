/*
* WebSecurityConfig
*  In this class we mention all apis to be restricted (authenticated) and which apis should not be restricted (authenticated) 
*
* 1.0
*
* @authored by Mritunjay Yadav
*/

package com.VersionDriveBackend.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

	@Autowired
	private UserDetailsService jwtUserDetailsService;

	@Autowired
	private JwtRequestFilter jwtRequestFilter;

	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
		
		/** configure AuthenticationManager so that it knows from where to load
		* user for matching credentials
		* Use BCryptPasswordEncoder
		*/ 
		auth.userDetailsService(jwtUserDetailsService).passwordEncoder(passwordEncoder());
	
	}

	/**
	 * @Description  PassWordEncoder using BCryptEcoder
	 * 
	 * @Author Mritunjay Yadav
	 * @return PasswordEncoder
	 * @param 
	 * @Exception 
	 * 
	 * */
	@Bean
	public PasswordEncoder passwordEncoder() {
		
		return new BCryptPasswordEncoder();
	
	}

	@Bean
	@Override
	public AuthenticationManager authenticationManagerBean() throws Exception {
		
		return super.authenticationManagerBean();
	
	}

	@Override
	protected void configure(HttpSecurity httpSecurity) throws Exception {
		
		// We don't need CSRF for this example
		httpSecurity.cors().and().csrf().disable()
				// dont authenticate this particular request
				.authorizeRequests().antMatchers("/authenticate").permitAll().
				// all other requests need to be authenticated
				anyRequest().authenticated().and().
				//.antMatchers("**/register/**").permitAll().and().
				// make sure we use stateless session; session won't be used to
				// store user's state.
				exceptionHandling().authenticationEntryPoint(jwtAuthenticationEntryPoint).and().sessionManagement()
				.sessionCreationPolicy(SessionCreationPolicy.STATELESS);

		// Add a filter to validate the tokens with every request
		httpSecurity.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);
	
	}
	
	
	/**
	 * @Description  Overriding configure method from WebSecurityConfigurerAdapter class
	 * 
	 * @Author Mritunjay Yadav
	 * @return void
	 * @param WebSecurity 
	 * @Exception Exception
	 * 
	 * */
	@Override
	public void configure(WebSecurity web) throws Exception {
	   
		//ignoring urls which should not be secured 
		web.ignoring().antMatchers("/register");
	    web.ignoring().antMatchers("/verification/**");
	    web.ignoring().antMatchers("/viewdownload/view/**");
	    web.ignoring().antMatchers("/viewdownload/download/**");
	    web.ignoring().antMatchers("/viewdownload/viewversion/**");
	    web.ignoring().antMatchers("/viewdownload/downloadversion/**");
	
	}
}
