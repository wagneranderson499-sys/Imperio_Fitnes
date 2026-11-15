package br.com.imperiofitness;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin")
public class AdminController {
 private final MatriculaRepository matriculas;
 private final AlunoRepository alunos;
 private final TreinoModeloRepository modelos;
 private final TreinoAtribuidoRepository atribuicoes;
 private final NotificacaoRepository notificacoes;
 private final PagamentoRepository pagamentos;
 public AdminController(MatriculaRepository matriculas,AlunoRepository alunos,TreinoModeloRepository modelos,
                        TreinoAtribuidoRepository atribuicoes,NotificacaoRepository notificacoes,PagamentoRepository pagamentos){
  this.matriculas=matriculas;this.alunos=alunos;this.modelos=modelos;this.atribuicoes=atribuicoes;
  this.notificacoes=notificacoes;this.pagamentos=pagamentos;
 }
 @GetMapping({"","/","/portal"})
 public String portal(Model model){
  List<Matricula> pedidos=matriculas.findAll();
  var lista=alunos.findAll();
  model.addAttribute("matriculas",pedidos);
  model.addAttribute("alunos",lista);
  model.addAttribute("modelos",modelos.findAll());
  model.addAttribute("notificacoes",notificacoes.findAll());
  model.addAttribute("totalPendentes",pedidos.stream().filter(m->"PENDENTE".equals(m.status)).count());
  model.addAttribute("totalAtivos",lista.stream().filter(a->"ATIVO".equals(a.situacaoPlano)).count());
  model.addAttribute("totalFacial",lista.stream().filter(a->!"CONCLUIDO".equals(a.cadastroFacial)).count());
  return "admin/dashboard";
 }
 @GetMapping(value="/home",produces=MediaType.TEXT_HTML_VALUE)
 @ResponseBody
 public ResponseEntity<byte[]> home() throws IOException{
  byte[] html=new ClassPathResource("static/admin-v4.html").getInputStream().readAllBytes();
  return ResponseEntity.ok().contentType(MediaType.parseMediaType("text/html;charset=UTF-8")).body(html);
 }
 private String done(RedirectAttributes redirect,String message,String anchor){
  redirect.addFlashAttribute("sucesso",message);return "redirect:/admin/portal#"+anchor;
 }
 private String fail(RedirectAttributes redirect,String message,String anchor){
  redirect.addFlashAttribute("erro",message);return "redirect:/admin/portal#"+anchor;
 }
 @PostMapping("/alunos")
 public String cadastrar(@RequestParam String nome,@RequestParam String email,@RequestParam String celular,
   @RequestParam String dataNascimento,@RequestParam String plano,@RequestParam String situacao,
   @RequestParam String facial,@RequestParam(required=false) Long modeloId,RedirectAttributes redirect){
  nome=nome.trim();email=email.trim().toLowerCase(Locale.ROOT);
  String tel=celular.replaceAll("\\D","");
  if(nome.length()<3||!nome.contains(" ")||!email.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")||
     tel.length()<10||tel.length()>11||!Set.of("AGUARDANDO","ATIVO","SUSPENSO","VENCIDO").contains(situacao))
   return fail(redirect,"Verifique nome completo, e-mail, celular e situação.","cadastrar");
  if(alunos.findByEmailIgnoreCase(email).isPresent())
   return fail(redirect,"Este e-mail já está cadastrado. Abra o aluno na listagem para gerenciá-lo.","cadastrar");
  LocalDate nascimento;
  try{nascimento=LocalDate.parse(dataNascimento);}catch(Exception e){return fail(redirect,"Data de nascimento inválida.","cadastrar");}
  if(!nascimento.isBefore(LocalDate.now())||nascimento.isBefore(LocalDate.now().minusYears(120)))
   return fail(redirect,"Data de nascimento inválida.","cadastrar");
  if(!Set.of("PELO_APLICATIVO","PENDENTE","CONCLUIDO").contains(facial))
   return fail(redirect,"Situação facial inválida.","cadastrar");
  byte[] bytes=new byte[32];new java.security.SecureRandom().nextBytes(bytes);
  String token=java.util.Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
  Aluno a=new Aluno(nome,email,"!CONTA_PENDENTE!",nascimento,tel);
  a.planoAtual=plano.isBlank()?null:plano.trim();
  a.situacaoPlano=situacao;a.cadastroFacial=facial;
  a.tokenAtivacao=token;a.expiracaoAtivacao=java.time.LocalDateTime.now().plusHours(48);
  alunos.save(a);
  if(modeloId!=null)modelos.findById(modeloId).ifPresent(m->atribuicoes.save(new TreinoAtribuido(a,m)));
  redirect.addFlashAttribute("sucesso","Aluno cadastrado. Entregue o link de ativação ao próprio aluno; ele define a senha.");
  redirect.addFlashAttribute("linkAtivacao","/ativar-conta?token="+token);
  return "redirect:/admin/portal#cadastrar";
 }
 @PostMapping("/alunos/{id}/plano")
 public String atualizarPlano(@PathVariable Long id,@RequestParam String plano,@RequestParam String situacao,
  @RequestParam(required=false) String vencimento,RedirectAttributes redirect){
  var found=alunos.findById(id);
  if(found.isEmpty())return fail(redirect,"Aluno não encontrado.","planos");
  if(plano.isBlank()||plano.length()>100||!Set.of("AGUARDANDO","ATIVO","SUSPENSO","VENCIDO","CANCELADO").contains(situacao))
   return fail(redirect,"Plano ou situação inválidos.","planos");
  Aluno a=found.get();a.planoAtual=plano.trim();a.situacaoPlano=situacao;
  if(vencimento!=null&&!vencimento.isBlank()){
   try{a.vencimentoPlano=LocalDate.parse(vencimento);}catch(Exception ex){return fail(redirect,"Data de vencimento inválida.","planos");}
  }
  alunos.save(a);return done(redirect,"Plano e situação atualizados.","planos");
 }
 @PostMapping("/alunos/{id}/pagamento")
 public String pagamento(@PathVariable Long id,@RequestParam BigDecimal valor,@RequestParam String forma,
  @RequestParam String vencimento,RedirectAttributes redirect){
  var found=alunos.findById(id);
  if(found.isEmpty())return fail(redirect,"Aluno não encontrado.","planos");
  if(valor.compareTo(BigDecimal.ZERO)<=0||valor.compareTo(new BigDecimal("100000"))>0||
    !Set.of("DINHEIRO","PIX","CARTAO","TRANSFERENCIA").contains(forma))
   return fail(redirect,"Valor ou forma de pagamento inválidos.","planos");
  LocalDate data;
  try{data=LocalDate.parse(vencimento);}catch(Exception e){return fail(redirect,"Vencimento inválido.","planos");}
  Aluno a=found.get();
  if(a.planoAtual==null||a.planoAtual.isBlank())return fail(redirect,"Defina um plano antes de registrar pagamento.","planos");
  pagamentos.save(new Pagamento(a,valor,forma,data));
  a.situacaoPlano="ATIVO";a.vencimentoPlano=data;alunos.save(a);
  return done(redirect,"Pagamento registrado e plano ativado.","planos");
 }
 @PostMapping("/matriculas/{id}/status")
 public String atualizarMatricula(@PathVariable Long id,@RequestParam String status,RedirectAttributes redirect){
  if(!Set.of("PENDENTE","APROVADA","RECUSADA","CANCELADA").contains(status))
   return fail(redirect,"Status inválido.","visao");
  var opt=matriculas.findById(id);
  if(opt.isEmpty())return fail(redirect,"Matrícula não encontrada.","visao");
  Matricula m=opt.get();m.status=status;matriculas.save(m);
  if("APROVADA".equals(status)){
   m.aluno.planoAtual=m.plano;
   if(!"ATIVO".equals(m.aluno.situacaoPlano))m.aluno.situacaoPlano="AGUARDANDO";
   alunos.save(m.aluno);
  }
  return done(redirect,"Situação da solicitação atualizada.","visao");
 }
 @PostMapping("/treinos/modelos")
 public String modelo(@RequestParam String nome,@RequestParam String divisao,@RequestParam String exercicios,
  @RequestParam(required=false) Long id,RedirectAttributes redirect){
  if(nome.isBlank()||nome.length()>120||exercicios.isBlank()||exercicios.length()>12000||
     !Set.of("ABC","AB","FUNCIONAL","OUTRO").contains(divisao))
   return fail(redirect,"Preencha nome, divisão e exercícios válidos.","treinos");
  TreinoModelo m;
  if(id!=null){var opt=modelos.findById(id);if(opt.isEmpty())return fail(redirect,"Modelo não encontrado.","treinos");m=opt.get();
   m.nome=nome.trim();m.divisao=divisao;m.exercicios=exercicios.trim();
  }else m=new TreinoModelo(nome.trim(),divisao,exercicios.trim());
  modelos.save(m);return done(redirect,"Modelo de treino salvo.","treinos");
 }
 @PostMapping("/treinos/modelos/{id}/excluir")
 public String excluir(@PathVariable Long id,RedirectAttributes redirect){
  if(!modelos.existsById(id))return fail(redirect,"Modelo não encontrado.","treinos");
  modelos.deleteById(id);return done(redirect,"Modelo excluído. Treinos já atribuídos permanecem com o aluno.","treinos");
 }
 @PostMapping("/treinos/atribuir")
 public String atribuir(@RequestParam Long alunoId,@RequestParam Long modeloId,RedirectAttributes redirect){
  var aluno=alunos.findById(alunoId);var modelo=modelos.findById(modeloId);
  if(aluno.isEmpty()||modelo.isEmpty())return fail(redirect,"Aluno ou modelo não encontrado.","treinos");
  atribuicoes.save(new TreinoAtribuido(aluno.get(),modelo.get()));
  return done(redirect,"Treino atribuído ao aluno.","treinos");
 }
 @PostMapping("/notificacoes")
 public String notificar(@RequestParam String titulo,@RequestParam String mensagem,@RequestParam String categoria,
  @RequestParam String destinatarios,@RequestParam(required=false) Long alunoId,RedirectAttributes redirect){
  if(titulo.isBlank()||titulo.length()>120||mensagem.isBlank()||mensagem.length()>2000||
    !Set.of("GERAL","TREINOS","MENSALIDADES","PROMOCOES").contains(categoria)||
    !Set.of("TODOS","ATIVOS","ALUNO").contains(destinatarios))
   return fail(redirect,"Confira os dados da notificação.","notificacoes");
  Aluno aluno=null;
  if("ALUNO".equals(destinatarios)){
   if(alunoId==null||alunos.findById(alunoId).isEmpty())return fail(redirect,"Selecione um aluno válido.","notificacoes");
   aluno=alunos.findById(alunoId).orElseThrow();
  }
  notificacoes.save(new Notificacao(titulo.trim(),mensagem.trim(),categoria,destinatarios,aluno));
  return done(redirect,"Notificação publicada no Portal do Aluno.","notificacoes");
 }
}
