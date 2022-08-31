package spring_security.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import spring_security.datatables.Datatables;
import spring_security.datatables.DatatablesColunas;
import spring_security.domain.Perfil;
import spring_security.domain.Usuario;
import spring_security.repository.UsuarioRepository;
import spring_security.web.exception.UserNotFound;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class UsuarioService implements UserDetailsService {

    @Autowired
    private UsuarioRepository repo;
    @Autowired
    private Datatables datatables;

    @Transactional(readOnly = true)
    public Usuario buscarPorEmail(String email){
        return repo.findByEmail(email);
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Usuario user = buscarPorEmail(username);
        return new User(
                user.getEmail(),
                user.getSenha(),
                AuthorityUtils.createAuthorityList(getAuthoritys(user.getPerfis()))
        );
    }

    private String[] getAuthoritys(List<Perfil> perfis){
        String[] arr = new String[perfis.size()];
        for(int i = 0; i < perfis.size(); i++){
            arr[i] = perfis.get(i).getDesc();
        }
        return arr;
    }

    @Transactional(readOnly = true)
    public Map<String, Object> buscarTodos(HttpServletRequest request) {
        datatables.setRequest(request);
        datatables.setColunas(DatatablesColunas.USUARIOS);
        Page<Usuario> page = datatables.getSearch().isEmpty() ?
                repo.findAll(datatables.getPageable()) :
                repo.findByEmailOrPerfil(datatables.getSearch(), datatables.getPageable());
        return datatables.getResponse(page);
    }

    @Transactional(readOnly = false)
    public void salvarUsuario(Usuario user) {
        String crypt = new BCryptPasswordEncoder().encode(user.getSenha());
        user.setSenha(crypt);
        repo.save(user);
    }

    @Transactional(readOnly = true)
    public Optional<Usuario> buscarPorId(Long id) {
        return repo.findById(id);
    }

    @Transactional(readOnly = true)
    public Usuario buscarPorIdEPerfis(Long userID) throws UserNotFound{
        return repo.findByIdAndPerfis(userID)
                .orElseThrow(() -> new UserNotFound("Usuário inexistente!"));
    }

    public static boolean isSenhaCorreta(String senhaDigitada, String senhaArmazenada) {
        return new BCryptPasswordEncoder().matches(senhaDigitada, senhaArmazenada);
    }

    @Transactional(readOnly = false)
    public void alterarSenha(Usuario user, String senha) {
        user.setSenha(new BCryptPasswordEncoder().encode(senha));
        repo.save(user);
    }
}
