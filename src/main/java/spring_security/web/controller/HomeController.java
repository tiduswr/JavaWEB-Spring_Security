package spring_security.web.controller;

import org.springframework.security.web.authentication.session.SessionAuthenticationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

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
	public String loginError(ModelMap map, HttpServletRequest request){
		HttpSession session = request.getSession();
		Exception lastException = (Exception) session.getAttribute("SPRING_SECURITY_LAST_EXCEPTION");
		map.addAttribute("alerta", "erro");

		if(lastException instanceof SessionAuthenticationException){
			map.addAttribute("titulo", "Acesso Recusado!");
			map.addAttribute("texto", "Você ja está logado em outro dispositivo.");
			map.addAttribute("subtexto", "Faça o logout no outro dispositivo.");
		}else{
			map.addAttribute("titulo", "Credenciais Inválidas!");
			map.addAttribute("texto", "Login ou senha incorretos, tente novamente.");
			map.addAttribute("subtexto", "Acesso permitido apenas para cadastros já ativados.");
		}
		return "login";
	}

	@GetMapping({"/expired"})
	public String expiredSession(ModelMap map){

		map.addAttribute("alerta", "erro");
		map.addAttribute("titulo", "Acesso recusado!");
		map.addAttribute("texto", "Sua sessão expirou.");
		map.addAttribute("subtexto", "Você logou em outro dispositivo.");

		return "login";
	}

	@GetMapping({"/acesso-negado"})
	public String acessoNegado(ModelMap map, HttpServletResponse response){
		map.addAttribute("status", response.getStatus());
		map.addAttribute("error", "Acesso Negado!");
		map.addAttribute("message", "Você não tem permissão de acesso para essa área.");
		return "error";
	}

}
