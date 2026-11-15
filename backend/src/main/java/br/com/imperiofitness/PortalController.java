package br.com.imperiofitness;

import jakarta.servlet.http.HttpSession;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.security.Principal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

@Controller
public class PortalController {
 private final AlunoRepository alunos;
 private final MatriculaRepository matriculas;
 private final PasswordEncoder encoder;
 public PortalController(AlunoRepository alunos,MatriculaRepository matriculas,PasswordEncoder encoder){
  this.alunos=alunos;this.matriculas=matriculas;this.encoder=encoder;
 }
 private String planoQuery(String plano){return plano==null||plano.isBlank()?"":"&plano="+URLEncoder.encode(plano,StandardCharsets.UTF_8);}
 @GetMapping("/cadastro")
 public String cadastro(@RequestParam(defaultValue="") String plano,@RequestParam(defaultValue="cadastro") String modo,Model model,HttpSession session){
  if(!plano.isBlank())session.setAttribute("planoEscolhido",plano);
  model.addAttribute("plano",plano);
  model.addAttribute("modo","login".equals(modo)?"login":"cadastro");
  return "cadastro";
 }
 @PostMapping("/cadastro")
 public String cadastrar(@RequestParam String nome,@RequestParam String email,@RequestParam String senha,
                         @RequestParam String dataNascimento,@RequestParam String celular,
                         @RequestParam(defaultValue="") String plano,RedirectAttributes redirect){
  nome=nome.trim().replaceAll("\\s+"," ");email=email.trim().toLowerCase(Locale.ROOT);
  String digitos=celular.replaceAll("\\D","");
  LocalDate nascimento;
  try{nascimento=LocalDate.parse(dataNascimento);}
  catch(DateTimeParseException ex){redirect.addFlashAttribute("erro","Informe uma data de nascimento válida.");return "redirect:/cadastro?modo=cadastro"+planoQuery(plano);}
  boolean senhaSegura=senha.length()>=10 && senha.length()<=72
      && senha.matches("(?s).*\\p{Lu}.*") && senha.matches("(?s).*\\p{Ll}.*")
      && senha.matches("(?s).*\\d.*") && senha.matches("(?s).*[^\\p{L}\\d\\s].*");
  if(nome.length()<3||!nome.contains(" ")||nome.length()>150||
      !email.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")||email.length()>254||
      !senhaSegura||nascimento.isAfter(LocalDate.now().minusDays(1))||
      nascimento.isBefore(LocalDate.now().minusYears(120))||
      !(digitos.length()==10||digitos.length()==11)){
   redirect.addFlashAttribute("erro","Confira os dados: nome completo, e-mail válido, senha forte, data de nascimento e celular com DDD.");
   return "redirect:/cadastro?modo=cadastro"+planoQuery(plano);
  }
  if(alunos.findByEmailIgnoreCase(email).isPresent()){
   redirect.addFlashAttribute("erro","E-mail já cadastrado. Entre na sua conta.");
   return "redirect:/cadastro?modo=login"+planoQuery(plano);
  }
  alunos.save(new Aluno(nome,email,encoder.encode(senha),nascimento,digitos));
  redirect.addFlashAttribute("sucesso","Conta criada! Entre para continuar sua matrícula.");
  return "redirect:/cadastro?modo=login"+planoQuery(plano);
 }
 @GetMapping("/login")
 public String login(@RequestParam(defaultValue="") String plano,@RequestParam(required=false) String error,Model model,HttpSession session){
  if(!plano.isBlank())session.setAttribute("planoEscolhido",plano);
  return "redirect:/cadastro?modo=login"+planoQuery(plano)+(error!=null?"&error":"");
 }
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
  if(plano==null||plano.isBlank()||plano.length()>100){redirect.addFlashAttribute("erro","Selecione um plano válido.");return "redirect:/portal";}
  Aluno aluno=alunos.findByEmailIgnoreCase(principal.getName()).orElseThrow();
  matriculas.save(new Matricula(aluno,plano.trim()));
  session.removeAttribute("planoEscolhido");
  redirect.addFlashAttribute("sucesso","Solicitação de matrícula recebida.");
  return "redirect:/portal";
 }
}
