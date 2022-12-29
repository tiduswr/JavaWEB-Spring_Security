package spring_security.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;
import spring_security.web.util.ThisApplication;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Service
public class EmailService {

    @Autowired
    JavaMailSender mailSender;

    @Autowired
    SpringTemplateEngine template;

    public void sendCreateAccountConfirmationEmail(String sendTo, String codigo) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED, "UTF-8");

        Context context = new Context();

        context.setVariable("titulo", "Bem vindo a clínica Spring Security");
        context.setVariable("texto", "Precisamos que confirme seu cadastro, clicando no link abaixo");
        context.setVariable("baseURL", ThisApplication.BASE_URL.getValue());
        context.setVariable("linkConfirmacao", ThisApplication.BASE_URL.getValue() +
                "/u/confirmacao/cadastro?codigo=" + codigo);

        String html = template.process("email/confirmacao", context);
        helper.setTo(sendTo);
        helper.setText(html, true);
        helper.setSubject("Confirmação de cadastro");
        helper.setFrom("nao-responder@clinica.com.br");

        helper.addInline("logo", new ClassPathResource("/static/image/spring-security.png"));

        mailSender.send(message);
    }

}
