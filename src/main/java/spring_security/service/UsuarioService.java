package spring_security.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import spring_security.domain.Perfil;
import spring_security.domain.Usuario;
import spring_security.repository.UsuarioRepository;

import java.util.List;

@Service
public class UsuarioService implements UserDetailsService {

    @Autowired
    private UsuarioRepository repo;

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
}
