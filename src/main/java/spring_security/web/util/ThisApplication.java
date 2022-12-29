package spring_security.web.util;

public enum ThisApplication {

    BASE_URL("http://localhost:8080/");

    private final String value;

    private ThisApplication(String value){
        this.value = value;
    }

    public String getValue(){
        return value;
    }

}
