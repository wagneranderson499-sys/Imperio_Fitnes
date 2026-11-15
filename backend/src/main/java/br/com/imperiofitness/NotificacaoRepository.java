package br.com.imperiofitness;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
public interface NotificacaoRepository extends JpaRepository<Notificacao,Long>{
 @Query("select n from Notificacao n left join n.aluno a where n.destinatarios='TODOS' or (n.destinatarios='ATIVOS' and :ativo=true) or (n.destinatarios='ALUNO' and a.id=:alunoId) order by n.criadaEm desc")
 List<Notificacao> paraAluno(@Param("alunoId") Long alunoId,@Param("ativo") boolean ativo);
}
