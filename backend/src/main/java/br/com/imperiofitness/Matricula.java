package br.com.imperiofitness;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name="matriculas")
public class Matricula {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id;
 @ManyToOne(optional=false) public Aluno aluno;
 @Column(nullable=false) public String plano;
 @Column(nullable=false) public String status="PENDENTE";
 @Column(nullable=false) public LocalDateTime criadaEm=LocalDateTime.now();
 protected Matricula(){}
 public Matricula(Aluno aluno,String plano){this.aluno=aluno;this.plano=plano;}
}
