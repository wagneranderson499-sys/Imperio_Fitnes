package br.com.imperiofitness;
import jakarta.persistence.*;
@Entity @Table(name="treino_modelos")
public class TreinoModelo {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id;
 @Column(nullable=false,length=120) public String nome;
 @Column(nullable=false,length=40) public String divisao;
 @Column(nullable=false,columnDefinition="TEXT") public String exercicios;
 protected TreinoModelo(){}
 public TreinoModelo(String nome,String divisao,String exercicios){this.nome=nome;this.divisao=divisao;this.exercicios=exercicios;}
}
