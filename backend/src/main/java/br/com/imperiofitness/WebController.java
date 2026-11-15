package br.com.imperiofitness;

import java.nio.charset.StandardCharsets;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import java.io.IOException;

@Controller
public class WebController {
 @GetMapping("/")
 public ResponseEntity<String> home() throws IOException {
  var resource=new ClassPathResource("static/home-v3.html");
  String html=new String(resource.getInputStream().readAllBytes(),StandardCharsets.UTF_8);
  String script="<script defer src=\"/js/home-actions.js\"></script>";
  html=html.replace("</body>",script+"</body>");
  return ResponseEntity.ok().contentType(MediaType.parseMediaType("text/html;charset=UTF-8")).body(html);
 }
}
