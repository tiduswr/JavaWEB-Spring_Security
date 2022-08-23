package spring_security.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

	// abrir pagina home
	@GetMapping({"/", "/home"})
	public String home() {
		return "home";
	}

	@GetMapping({"/login"})
	public String login(){
		return "login";
	}

	@GetMapping({"/login-error"})
	public String loginError(ModelMap map){
		map.addAttribute("alerta", "erro");
		map.addAttribute("titulo", "Credenciais Inválidas!");
		map.addAttribute("texto", "Login ou senha incorretos, tente novamente.");
		map.addAttribute("subtexto", "Acesso permitido apenas para cadastros já ativados.");
		return "login";
	}

}
