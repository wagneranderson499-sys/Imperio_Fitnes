package br.com.imperiofitness;
import jakarta.persistence.*;
import java.time.LocalDateTime;
@Entity @Table(name="notificacoes")
public class Notificacao {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id;
 @Column(nullable=false,length=120) public String titulo;
 @Column(nullable=false,columnDefinition="TEXT") public String mensagem;
 @Column(nullable=false,length=40) public String categoria;
 @Column(nullable=false,length=20) public String destinatarios;
 @ManyToOne public Aluno aluno;
 @Column(nullable=false) public LocalDateTime criadaEm=LocalDateTime.now();
 protected Notificacao(){}
 public Notificacao(String titulo,String mensagem,String categoria,String destinatarios,Aluno aluno){
  this.titulo=titulo;this.mensagem=mensagem;this.categoria=categoria;this.destinatarios=destinatarios;this.aluno=aluno;
 }
}
