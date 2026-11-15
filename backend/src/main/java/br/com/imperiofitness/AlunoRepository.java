package br.com.imperiofitness;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
public interface AlunoRepository extends JpaRepository<Aluno,Long> {
 Optional<Aluno> findByEmailIgnoreCase(String email);
 Optional<Aluno> findByTokenAtivacao(String token);
}
