package br.com.imperiofitness;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.security.Principal;
import java.util.Locale;

@Controller
public class PortalController {
 private final AlunoRepository alunos;
 private final MatriculaRepository matriculas;
 private final PasswordEncoder encoder;
 public PortalController(AlunoRepository alunos,MatriculaRepository matriculas,PasswordEncoder encoder){
  this.alunos=alunos;this.matriculas=matriculas;this.encoder=encoder;
 }
 @GetMapping("/cadastro")
 public String cadastro(@RequestParam(defaultValue="") String plano,Model model,HttpSession session){if(!plano.isBlank())session.setAttribute("planoEscolhido",plano);model.addAttribute("plano",plano);return "cadastro";}
 @PostMapping("/cadastro")
 public String cadastrar(@RequestParam String nome,@RequestParam String email,@RequestParam String senha,
                         @RequestParam(defaultValue="") String plano,RedirectAttributes redirect){
  nome=nome.trim();email=email.trim().toLowerCase(Locale.ROOT);
  if(nome.isBlank()||!email.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")||senha.length()<8){
   redirect.addFlashAttribute("erro","Preencha nome, e-mail valido e senha de pelo menos 8 caracteres.");
   return "redirect:/cadastro"+(plano.isBlank()?"":"?plano="+java.net.URLEncoder.encode(plano,java.nio.charset.StandardCharsets.UTF_8));
  }
  if(alunos.findByEmailIgnoreCase(email).isPresent()){redirect.addFlashAttribute("erro","E-mail ja cadastrado. Entre na sua conta.");return "redirect:/login";}
  alunos.save(new Aluno(nome,email,encoder.encode(senha)));
  redirect.addFlashAttribute("sucesso","Conta criada! Entre para continuar.");
  return "redirect:/login"+(plano.isBlank()?"":"?plano="+java.net.URLEncoder.encode(plano,java.nio.charset.StandardCharsets.UTF_8));
 }
 @GetMapping("/login")
 public String login(@RequestParam(defaultValue="") String plano,Model model,HttpSession session){if(!plano.isBlank())session.setAttribute("planoEscolhido",plano);model.addAttribute("plano",plano);return "login";}
 @GetMapping("/portal")
 public String portal(Principal principal,Model model,HttpSession session){
  model.addAttribute("planoEscolhido",session.getAttribute("planoEscolhido"));
  Aluno aluno=alunos.findByEmailIgnoreCase(principal.getName()).orElseThrow();
  model.addAttribute("aluno",aluno);
  model.addAttribute("matriculas",matriculas.findByAlunoIdOrderByCriadaEmDesc(aluno.id));
  return "portal";
 }
 @PostMapping("/portal/matriculas")
 public String matricular(Principal principal,@RequestParam String plano,RedirectAttributes redirect,HttpSession session){
  if(plano==null||plano.isBlank()||plano.length()>100){redirect.addFlashAttribute("erro","Selecione um plano valido.");return "redirect:/portal";}
  Aluno aluno=alunos.findByEmailIgnoreCase(principal.getName()).orElseThrow();
  matriculas.save(new Matricula(aluno,plano.trim()));
  session.removeAttribute("planoEscolhido");
  redirect.addFlashAttribute("sucesso","Solicitacao de matricula recebida.");
  return "redirect:/portal";
 }
}
