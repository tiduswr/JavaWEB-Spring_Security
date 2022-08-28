package spring_security.web.conversor;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import spring_security.domain.Perfil;

import java.util.ArrayList;
import java.util.List;

@Component
public class PerfisConverter implements Converter<String[], List<Perfil>> {
    @Override
    public List<Perfil> convert(String[] source) {
        List<Perfil> perfis = new ArrayList<>();

        for(String id : source){
            perfis.add(new Perfil(Long.parseLong(id)));
        }

        return perfis;
    }
}
