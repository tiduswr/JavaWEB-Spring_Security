package spring_security.web.conversor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import spring_security.domain.Especialidade;
import spring_security.service.EspecialidadeService;

import java.util.HashSet;
import java.util.Set;

@Component
public class EspecialidadesConverter implements Converter<String[], Set<Especialidade>> {

    @Autowired
    private EspecialidadeService service;

    @Override
    public Set<Especialidade> convert(String[] source) {
        Set<Especialidade> especialidades = new HashSet<>();

        if(source != null && source.length > 0){
            especialidades.addAll(service.buscarPorTitulos(source));
        }

        return especialidades;
    }
}
