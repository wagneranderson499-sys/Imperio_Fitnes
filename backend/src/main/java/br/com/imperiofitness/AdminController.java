package br.com.imperiofitness;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdminController {
 private final MatriculaRepository matriculas;
 public AdminController(MatriculaRepository matriculas){this.matriculas=matriculas;}
 @GetMapping({"/admin","/admin/"})
 public String admin(Model model){
  model.addAttribute("matriculas",matriculas.findAll());
  return "admin/dashboard";
 }
}
