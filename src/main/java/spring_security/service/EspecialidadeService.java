package spring_security.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import spring_security.datatables.Datatables;
import spring_security.datatables.DatatablesColunas;
import spring_security.domain.Especialidade;
import spring_security.repository.EspecialidadeRepository;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.Optional;

@Service
public class EspecialidadeService {

    @Autowired
    private EspecialidadeRepository repo;
    @Autowired
    private Datatables datatables;

    @Transactional(readOnly = false)
    public void salvar(Especialidade especialidade) {
        repo.save(especialidade);
    }

    @Transactional(readOnly = true)
    public Map<String, Object> buscarEspecialidades(HttpServletRequest request) {
        datatables.setRequest(request);
        datatables.setColunas(DatatablesColunas.ESPECIALIDADES);
        Page<?> page = datatables.getSearch().isEmpty()
                        ? repo.findAll(datatables.getPageable())
                        : repo.findAllByTitulo(datatables.getSearch(), datatables.getPageable());
        return datatables.getResponse(page);
    }

    @Transactional(readOnly = true)
    public Optional<Especialidade> buscarPorID(Long id) {
        return repo.findById(id);
    }

    @Transactional(readOnly = false)
    public void excluirPorID(Long id) {
        repo.deleteById(id);
    }
}
