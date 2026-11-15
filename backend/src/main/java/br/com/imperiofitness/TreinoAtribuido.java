package br.com.imperiofitness;
import jakarta.persistence.*;
import java.time.LocalDateTime;
@Entity @Table(name="treinos_atribuidos")
public class TreinoAtribuido {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id;
 @ManyToOne(optional=false) public Aluno aluno;
 @Column(nullable=false,length=120) public String nome;
 @Column(nullable=false,length=40) public String divisao;
 @Column(nullable=false,columnDefinition="TEXT") public String exercicios;
 @Column(nullable=false) public LocalDateTime criadoEm=LocalDateTime.now();
 protected TreinoAtribuido(){}
 public TreinoAtribuido(Aluno aluno,TreinoModelo modelo){this.aluno=aluno;this.nome=modelo.nome;this.divisao=modelo.divisao;this.exercicios=modelo.exercicios;}
}
