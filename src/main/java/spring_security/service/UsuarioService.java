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
import org.springframework.util.Base64Utils;
import spring_security.datatables.Datatables;
import spring_security.datatables.DatatablesColunas;
import spring_security.domain.Perfil;
import spring_security.domain.PerfilTipo;
import spring_security.domain.Usuario;
import spring_security.repository.UsuarioRepository;
import spring_security.web.exception.AcessoNegadoException;
import spring_security.web.exception.UserNotFound;
import spring_security.web.util.RandomAlphanumeric;

import javax.mail.MessagingException;
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
    @Autowired
    private EmailService emailService;

    @Transactional(readOnly = true)
    public Usuario buscarPorEmail(String email){
        return repo.findByEmail(email);
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Usuario user = buscarPorEmailEAtivo(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario " + username + " não encontrado/ativo!"));
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

    @Transactional(readOnly = false)
    public void salvarCadastroPaciente(Usuario user) throws MessagingException {
        String crypt = new BCryptPasswordEncoder().encode(user.getSenha());
        user.setSenha(crypt);
        user.addPerfil(PerfilTipo.PACIENTE);
        repo.save(user);

        emailDecConfirmacaoCadastro(user.getEmail());
    }

    @Transactional(readOnly = true)
    public Optional<Usuario> buscarPorEmailEAtivo(String email){
        return repo.findByEmailAndAtivo(email);
    }

    public void emailDecConfirmacaoCadastro(String email) throws MessagingException {
        String codigo = Base64Utils.encodeToString(email.getBytes());
        emailService.sendCreateAccountConfirmationEmail(email, codigo);
    }

    @Transactional(readOnly = false)
    public void ativarCadastroPaciente(String code) throws AcessoNegadoException{
        String email = new String(Base64Utils.decodeFromString(code));

        Usuario user = buscarPorEmail(email);
        if(user == null || user.hasNotId())
            throw new AcessoNegadoException("Não foi possivel ativar seu cadastro, entre em contato com o suporte!");

        user.setAtivo(true);

        repo.save(user);
    }

    @Transactional(readOnly = false)
    public void pedidoRedefinicaoSenha(String email) throws MessagingException {
        Usuario user = buscarPorEmailEAtivo(email).orElseThrow(
                () -> new UsernameNotFoundException("Usuario " + email + " não encontrado!")
        );

        String verificador = RandomAlphanumeric.generateRandomAlphaNumeric(6);

        user.setCodigoVerificador(verificador);

        emailService.enviarPedidoRedefinicaoSenha(email, verificador);
    }
}
