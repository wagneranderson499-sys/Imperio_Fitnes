package br.com.imperiofitness;

import jakarta.persistence.*;

@Entity
@Table(name="alunos", uniqueConstraints=@UniqueConstraint(columnNames="email"))
public class Aluno {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id;
 @Column(nullable=false) public String nome;
 @Column(nullable=false,unique=true) public String email;
 @Column(nullable=false) public String senhaHash;
 @Column(nullable=false) public String papel="CLIENTE";
 protected Aluno() {}
 public Aluno(String nome,String email,String senhaHash){this.nome=nome;this.email=email;this.senhaHash=senhaHash;}
}
