package spring_security.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import spring_security.domain.Perfil;
import spring_security.domain.PerfilTipo;
import spring_security.service.UsuarioService;

@Configuration
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
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain configureFilterChain(HttpSecurity http) throws Exception{
        http.authorizeRequests()
            .antMatchers("/webjars/**", "/css/**", "/js/**", "/image/**").permitAll()
            .antMatchers("/", "/home").permitAll()

                .antMatchers("/u/**").hasAuthority(ADMIN)
                .antMatchers("/medicos/**").hasAuthority(MEDICO)
                .antMatchers("/especialidades/**").hasAuthority(ADMIN)
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
                    .accessDeniedPage("/acesso-negado");

        return http.build();
    }

}
