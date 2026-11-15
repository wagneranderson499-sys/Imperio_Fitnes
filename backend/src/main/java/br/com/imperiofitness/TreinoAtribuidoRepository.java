package br.com.imperiofitness;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface TreinoAtribuidoRepository extends JpaRepository<TreinoAtribuido,Long>{
 List<TreinoAtribuido> findByAlunoIdOrderByCriadoEmDesc(Long alunoId);
}
