package br.com.imperiofitness;

import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin")
public class AdminController {
 private final MatriculaRepository matriculas;
 private final AlunoRepository alunos;
 public AdminController(MatriculaRepository matriculas,AlunoRepository alunos){
  this.matriculas=matriculas;this.alunos=alunos;
 }
 @GetMapping({"","/"})
 public String admin(Model model){
  List<Matricula> pedidos=matriculas.findAll();
  model.addAttribute("matriculas",pedidos);
  model.addAttribute("alunos",alunos.findAll());
  model.addAttribute("totalPendentes",pedidos.stream().filter(m->"PENDENTE".equals(m.status)).count());
  model.addAttribute("totalAprovadas",pedidos.stream().filter(m->"APROVADA".equals(m.status)).count());
  return "admin/dashboard";
 }
 @PostMapping("/matriculas/{id}/status")
 public String atualizarMatricula(@PathVariable Long id,@RequestParam String status,RedirectAttributes redirect){
  if(!List.of("PENDENTE","APROVADA","RECUSADA","CANCELADA").contains(status)){
   redirect.addFlashAttribute("erro","Status inválido.");
   return "redirect:/admin#matriculas";
  }
  var opt=matriculas.findById(id);
  if(opt.isEmpty()){
   redirect.addFlashAttribute("erro","Matrícula não encontrada.");
   return "redirect:/admin#matriculas";
  }
  Matricula matricula=opt.get();
  matricula.status=status;
  matriculas.save(matricula);
  redirect.addFlashAttribute("sucesso","Status da matrícula atualizado.");
  return "redirect:/admin#matriculas";
 }
}
