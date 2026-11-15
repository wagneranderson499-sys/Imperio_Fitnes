package br.com.imperiofitness;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.math.BigDecimal;
@Entity @Table(name="pagamentos")
public class Pagamento {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id;
 @ManyToOne(optional=false) public Aluno aluno;
 @Column(nullable=false,precision=10,scale=2) public BigDecimal valor;
 @Column(nullable=false,length=30) public String forma;
 @Column(nullable=false) public LocalDateTime registradoEm=LocalDateTime.now();
 public LocalDate vencimento;
 protected Pagamento(){}
 public Pagamento(Aluno aluno,BigDecimal valor,String forma,LocalDate vencimento){this.aluno=aluno;this.valor=valor;this.forma=forma;this.vencimento=vencimento;}
}
