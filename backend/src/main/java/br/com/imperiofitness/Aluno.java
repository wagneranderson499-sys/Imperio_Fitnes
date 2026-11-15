package br.com.imperiofitness;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name="alunos", uniqueConstraints=@UniqueConstraint(columnNames="email"))
public class Aluno {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id;
 @Column(nullable=false) public String nome;
 @Column(nullable=false,unique=true) public String email;
 @Column(nullable=false) public String senhaHash;
 @Column(nullable=false) public String papel="CLIENTE";
 @Column(name="data_nascimento") public LocalDate dataNascimento;
 @Column(name="celular",length=20) public String celular;
 @Column(nullable=false) public boolean notificacoesAtivas=true;
 @Column(length=100) public String planoAtual;
 @Column(nullable=false,length=24) public String situacaoPlano="AGUARDANDO";
 @Column(nullable=false,length=24) public String cadastroFacial="PELO_APLICATIVO";
 public java.time.LocalDate vencimentoPlano;
 protected Aluno() {}
 public Aluno(String nome,String email,String senhaHash){this.nome=nome;this.email=email;this.senhaHash=senhaHash;}
 public Aluno(String nome,String email,String senhaHash,LocalDate dataNascimento,String celular){
  this(nome,email,senhaHash);this.dataNascimento=dataNascimento;this.celular=celular;
 }
}
