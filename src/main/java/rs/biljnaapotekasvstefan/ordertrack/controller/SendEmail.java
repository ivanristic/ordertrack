package rs.biljnaapotekasvstefan.ordertrack.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;
import rs.biljnaapotekasvstefan.ordertrack.model.Emails;
import rs.biljnaapotekasvstefan.ordertrack.repository.EmailsRepository;

import java.util.List;

@Component
public class SendEmail {
    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    private EmailsRepository emailRepository;

    public void sendEmails(String emailBody, String[] emailAddresses) {
        List<Emails> emails = (List<Emails>) emailRepository.findAll();
        //String[] s = emails.stream().map(e -> e.getEmail()).toArray(String[]::new);
        //System.out.println(s);
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setFrom("porudzbine@biljnaapotekasvstefan.rs");
        msg.setTo(emailAddresses);
        //msg.setTo("tanja978@gmail.com");

        msg.setSubject("Statusi porudžbina");
        msg.setText(emailBody);
        //msg.setText("Pozdrav \n Ovo šaljem sa aplikacije za praćenje pošiljaka");

        javaMailSender.send(msg);

    }
}
