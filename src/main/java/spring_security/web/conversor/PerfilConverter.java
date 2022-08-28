package spring_security.web.conversor;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import spring_security.domain.Perfil;

@Component
public class PerfilConverter implements Converter<String, Perfil> {
    @Override
    public Perfil convert(String source) {
        return new Perfil(Long.parseLong(source));
    }
}
