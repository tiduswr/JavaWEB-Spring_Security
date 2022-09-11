package spring_security.web.exception;

public class RestrictedArea extends RuntimeException{

    public RestrictedArea(String message){
        super(message);
    }

}
