package spring_security.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.session.RegisterSessionAuthenticationStrategy;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.security.web.session.HttpSessionEventPublisher;
import spring_security.domain.PerfilTipo;
import spring_security.service.UsuarioService;

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true) //Ativa a opção de comentar metodos para determinados perfis
@EnableWebSecurity
public class SecurityConfig {

    private static final String ADMIN = PerfilTipo.ADMIN.getDesc();
    private static final String MEDICO = PerfilTipo.MEDICO.getDesc();
    private static final String PACIENTE = PerfilTipo.PACIENTE.getDesc();

    @Autowired
    private UsuarioService userServ;

    @Bean
    public AuthenticationManager configureAuthentication(AuthenticationConfiguration auth) throws Exception{
        return auth.getAuthenticationManager();
    }

    @Bean
    public SessionRegistry sessionRegistry(){
        return new SessionRegistryImpl();
    }

    @Bean
    public ServletListenerRegistrationBean<?> servletListenerRegistrationBean(){
        return new ServletListenerRegistrationBean<>(new HttpSessionEventPublisher());
    }

    @Bean
    public SessionAuthenticationStrategy sessionAuthStrategy(){
        return new RegisterSessionAuthenticationStrategy(sessionRegistry());
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain configureFilterChain(HttpSecurity http) throws Exception{
        http.authorizeRequests()
                //PUBLICO
                .antMatchers("/webjars/**", "/css/**", "/js/**", "/image/**").permitAll()
                .antMatchers("/", "/home").permitAll()
                .antMatchers("/u/novo/cadastro","/u/cadastro/realizado"
                        ,"/u/cadastro/paciente/salvar","/u/confirmacao/cadastro", "/u/p/**", "/expired").permitAll()
                //ADMIN
                .antMatchers("/u/editar/senha", "/u/confirmar/senha").hasAnyAuthority(PACIENTE, MEDICO)
                .antMatchers("/u/**").hasAuthority(ADMIN)
                .antMatchers("/especialidades/datatables/server/medico/*").hasAnyAuthority(ADMIN, MEDICO)
                .antMatchers("/especialidades/titulo").hasAnyAuthority(ADMIN, MEDICO, PACIENTE)
                .antMatchers("/especialidades/**").hasAuthority(ADMIN)

                //MEDICO
                .antMatchers("/medicos/especialidade/titulo/*").hasAnyAuthority(PACIENTE, MEDICO)
                .antMatchers("/medicos/dados", "/medicos/salvar", "/medicos/editar")
                    /*É necessário informar Medico e ADMIN porque se informar apenas um,
                    o SPRING vai bloquear essas URLS para todos os outros perfis*/
                    .hasAnyAuthority(MEDICO, ADMIN)
                .antMatchers("/medicos/**").hasAuthority(MEDICO)

                //PACIENTE
                .antMatchers("/pacientes/**").hasAuthority(PACIENTE)
                .anyRequest().authenticated()

                //Mapeando login
                .and()
                    .formLogin()
                    .loginPage("/login")
                    .defaultSuccessUrl("/", true)
                    .failureUrl("/login-error")
                    .permitAll()
                //Mapeando logout
                .and()
                    .logout()
                    .logoutSuccessUrl("/")
                //Mapeando Erros
                .and()
                    .exceptionHandling()
                    .accessDeniedPage("/acesso-negado")
                .and().rememberMe();

        http.sessionManagement()
                .maximumSessions(1)
                .expiredUrl("/expired")
                .maxSessionsPreventsLogin(false)
                .sessionRegistry(sessionRegistry());

        http.sessionManagement()
                .sessionFixation().newSession()
                .sessionAuthenticationStrategy(sessionAuthStrategy());

        return http.build();
    }

}
