package br.com.gbvbahia.threads.monitor.cfg;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
public class SecurityCfg extends WebSecurityConfigurerAdapter {

  @Override
  protected void configure(HttpSecurity http) throws Exception {

    http.csrf().disable()
        .httpBasic().and()                      
        .authorizeRequests()
        .antMatchers("/**").permitAll()
        .anyRequest().permitAll();
    
    http.headers()
    .frameOptions()
    .sameOrigin();
  }
}
