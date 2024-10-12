package tn.esprit.user.utils;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@Configuration
public class MailConfigurations {

    @Bean
    public JavaMailSender getJavaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost("smtp.gmail.com");
        mailSender.setPort(587); // Use 465 if using SSL

        // Replace with your app-specific password if needed
        mailSender.setUsername("elmejri.sami@gmail.com");
        mailSender.setPassword("rhzb flwv krgh iway");

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.starttls.required", "true");
        props.put("mail.smtp.ssl.trust", "smtp.gmail.com"); // trust Gmail's certificate

        // Enable detailed logging of mail activity
        props.put("mail.debug", "true");

        return mailSender;
    }
}
