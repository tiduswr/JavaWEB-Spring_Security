package spring_security.web.exception;

public class AcessoNegadoException extends RuntimeException{
    public AcessoNegadoException(String message){
        super(message);
    }
}
