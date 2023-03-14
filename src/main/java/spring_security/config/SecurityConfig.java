package spring_security.config;

import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
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
@EnableMethodSecurity
@EnableWebSecurity
public class SecurityConfig {

    private static final String ADMIN = PerfilTipo.ADMIN.getDesc();
    private static final String MEDICO = PerfilTipo.MEDICO.getDesc();
    private static final String PACIENTE = PerfilTipo.PACIENTE.getDesc();

    @Bean
    public AuthenticationManager configureAuthentication(HttpSecurity http,
                                                         PasswordEncoder passwordEncoder,
                                                         UsuarioService userDetailService) throws Exception{

        return http.getSharedObject(AuthenticationManagerBuilder.class)
                .userDetailsService(userDetailService)
                .passwordEncoder(passwordEncoder)
                .and().build();
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
        http.authorizeHttpRequests((authorize) ->
                authorize
                //PUBLICO
                .requestMatchers("/webjars/**", "/css/**", "/js/**", "/image/**").permitAll()
                .requestMatchers("/", "/home").permitAll()
                .requestMatchers("/u/novo/cadastro","/u/cadastro/realizado"
                        ,"/u/cadastro/paciente/salvar","/u/confirmacao/cadastro", "/u/p/**", "/expired").permitAll()
                //ADMIN
                .requestMatchers("/u/editar/senha", "/u/confirmar/senha").hasAnyAuthority(PACIENTE, MEDICO)
                .requestMatchers("/u/**").hasAuthority(ADMIN)
                .requestMatchers("/especialidades/datatables/server/medico/*").hasAnyAuthority(ADMIN, MEDICO)
                .requestMatchers("/especialidades/titulo").hasAnyAuthority(ADMIN, MEDICO, PACIENTE)
                .requestMatchers("/especialidades/**").hasAuthority(ADMIN)

                //MEDICO
                .requestMatchers("/medicos/especialidade/titulo/*").hasAnyAuthority(PACIENTE, MEDICO)
                .requestMatchers("/medicos/dados", "/medicos/salvar", "/medicos/editar")
                    /*É necessário informar Medico e ADMIN porque se informar apenas um,
                    o SPRING vai bloquear essas URLS para todos os outros perfis*/
                    .hasAnyAuthority(MEDICO, ADMIN)
                .requestMatchers("/medicos/**").hasAuthority(MEDICO)

                //PACIENTE
                .requestMatchers("/pacientes/**").hasAuthority(PACIENTE)
                .anyRequest().authenticated())

                //Mapeando login
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
