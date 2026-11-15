package br.com.imperiofitness;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.Locale;

@Configuration
public class AdminBootstrap {
 @Bean
 CommandLineRunner provisionarAdmin(AlunoRepository alunos,PasswordEncoder encoder,
  @Value("${ADMIN_EMAIL:}") String email,
  @Value("${ADMIN_PASSWORD:}") String senha){
  return args->{
   if(email.isBlank()||senha.isBlank())return;
   if(senha.length()<12)throw new IllegalArgumentException("ADMIN_PASSWORD precisa ter no mínimo 12 caracteres.");
   String normalizado=email.trim().toLowerCase(Locale.ROOT);
   var existente=alunos.findByEmailIgnoreCase(normalizado);
   if(existente.isPresent()){
    // Nunca eleva automaticamente um cliente existente a administrador.
    return;
   }
   Aluno admin=new Aluno("Administrador Império",normalizado,encoder.encode(senha));
   admin.papel="ADMIN";
   alunos.save(admin);
  };
 }
}
