package spring_security.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import spring_security.domain.Paciente;
import spring_security.repository.PacienteRepository;

@Service
public class PacienteService {

    @Autowired
    private PacienteRepository repo;

    @Transactional(readOnly = false)
    public Paciente buscarPorUsuarioEmail(String email){
        return repo.findByUsuarioEmail(email).orElse(new Paciente());
    }

    @Transactional(readOnly = false)
    public void salvar(Paciente paciente) {
        repo.save(paciente);
    }

    @Transactional(readOnly = false)
    public void editar(Paciente paciente) {
        Paciente persistente = repo.findById(paciente.getId()).get();

        persistente.setNome(paciente.getNome());
        persistente.setDtNascimento(paciente.getDtNascimento());

        repo.save(persistente);
    }
}
