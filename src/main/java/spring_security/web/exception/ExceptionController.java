package spring_security.web.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

@ControllerAdvice
public class ExceptionController {

    @ExceptionHandler(UserNotFound.class)
    public ModelAndView usuarioNaoEncontradoException(UserNotFound e){
        ModelAndView error = new ModelAndView("error");
        error.addObject("status", HttpStatus.NOT_FOUND.value());
        error.addObject("error", "Operação não pode ser Realizada.");
        error.addObject("message", e.getMessage());
        return error;
    }

    @ExceptionHandler(RestrictedArea.class)
    public ModelAndView areaRestritaException(RestrictedArea e){
        ModelAndView error = new ModelAndView("error");
        error.addObject("status", HttpStatus.FORBIDDEN.value());
        error.addObject("error", "Área Restrita!");
        error.addObject("message", e.getMessage());
        return error;
    }

}
