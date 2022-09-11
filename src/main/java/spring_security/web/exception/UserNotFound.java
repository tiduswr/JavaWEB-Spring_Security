package spring_security.web.exception;

public class UserNotFound extends RuntimeException{

    public UserNotFound(String message){
        super(message);
    }

}
