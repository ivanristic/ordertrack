package rs.biljnaapotekasvstefan.ordertrack.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;
import rs.biljnaapotekasvstefan.ordertrack.model.Email;
import rs.biljnaapotekasvstefan.ordertrack.repository.EmailRepository;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class SendEmail {
    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    private EmailRepository emailRepository;

    public void sendEmails(String emailBody) {
        List<Email> emails = (List<Email>) emailRepository.findAll();
        String[] s = emails.stream().map(e -> e.getEmail()).toArray(String[]::new);
        //System.out.println(s);
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setFrom("porudzbine@biljnaapotekasvstefan.rs");
        msg.setTo(s);
        //msg.setTo("tanja978@gmail.com");

        msg.setSubject("Statusi porudžbina");
        msg.setText(emailBody);
        //msg.setText("Pozdrav \n Ovo šaljem sa aplikacije za praćenje pošiljaka");

        javaMailSender.send(msg);

    }
}
