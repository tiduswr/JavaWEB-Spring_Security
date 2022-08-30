package spring_security.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import spring_security.domain.Medico;
import spring_security.repository.MedicoRepository;

@Service
public class MedicoService {

    @Autowired
    private MedicoRepository medicoRepository;

    @Transactional(readOnly = true)
    public Medico buscarPorUsuarioID(Long id){
        return medicoRepository.findByUsuarioId(id)
                .orElse(new Medico());
    }

    @Transactional(readOnly = false)
    public void salvar(Medico medico) {
        medicoRepository.save(medico);
    }

    @Transactional(readOnly = false)
    public void editar(Medico medico){
        Medico persistentMedico = medicoRepository.findById(medico.getId()).get();
        persistentMedico.setCrm(medico.getCrm());
        persistentMedico.setNome(medico.getNome());
        persistentMedico.setDtInscricao(medico.getDtInscricao());
        if(!medico.getEspecialidades().isEmpty())
            persistentMedico.getEspecialidades().addAll(medico.getEspecialidades());
        medicoRepository.save(persistentMedico);
    }

    @Transactional(readOnly = true)
    public Medico buscarPorEmail(String username) {
        return medicoRepository.findMedicoByEmail(username).
                orElse(new Medico());
    }
}
