package br.com.imperiofitness;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {
 @Bean PasswordEncoder passwordEncoder(){return new BCryptPasswordEncoder();}
 @Bean UserDetailsService userDetailsService(AlunoRepository alunos){
  return username -> alunos.findByEmailIgnoreCase(username)
    .map(a -> User.withUsername(a.email).password(a.senhaHash).roles(a.papel).build())
    .orElseThrow(() -> new org.springframework.security.core.userdetails.UsernameNotFoundException("Conta nao encontrada"));
 }
 @Bean SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
  return http.authorizeHttpRequests(auth -> auth
      .requestMatchers("/portal/**").authenticated()
      .requestMatchers("/admin/**","/admin","/admin-v4.html").denyAll()
      .anyRequest().permitAll())
    .formLogin(form -> form.loginPage("/login").loginProcessingUrl("/login").defaultSuccessUrl("/portal",false).permitAll())
    .logout(logout -> logout.logoutSuccessUrl("/"))
    .build();
 }
}
