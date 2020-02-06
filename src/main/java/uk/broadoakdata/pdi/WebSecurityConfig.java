package uk.broadoakdata.pdi;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;

public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    /*
    By default, Spring framework creates a default user user with a random password. Then, the framework prints out the password to logs. It looks like the following:
Using default security password: e94e0e35-f60d-454e-90d6-3f4b981237f8
https://howtodoinjava.com/spring-boot2/security-rest-basic-auth-example/
https://octoperf.com/blog/2018/02/22/spring-boot-rest-tutorial/
     */

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .anyRequest()
                .authenticated()
                .and()
                .httpBasic();
    }
}