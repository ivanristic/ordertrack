package rs.biljnaapotekasvstefan.ordertrack.controller;

import jakarta.annotation.PostConstruct;
import jakarta.mail.*;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.search.FlagTerm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;


@Component
public class ReceiveEmail {


    @Value("${spring.mail.username}")
    String username;
    @Value("${spring.mail.password}")
    String password;
    @Value("${spring.mail.host}")
    String server;
    @Value("${spring.mail.port}")
    String port;
    Properties properties = new Properties();
    Store store;
    Folder receiveFolder;
    @PostConstruct
    void setup() throws MessagingException {
        properties.put("mail.imap.host", server);
        properties.put("mail.imap.port", port);
        properties.put("mail.store.protocol", "imaps");
        Session emailSession = Session.getDefaultInstance(properties);
        store = emailSession.getStore("imaps");
        store.connect(server, username, password);

        receiveFolder = store.getFolder("Inbox");
        receiveFolder.open(Folder.READ_ONLY);

    }

    public InputStream read() throws MessagingException, IOException {
        Message[] messages = receiveFolder.search(
                new FlagTerm(new Flags(Flags.Flag.SEEN), false));

        for (int i = 0; i < messages.length; i++) {
            Message message = messages[i];
            //String contentType = message.getContentType();

            if (message.getContentType().contains("multipart") &&
                    (message.getSubject().contains("Pošiljka") || message.getSubject().contains("Пошиљка"))) {
                Multipart multiPart = (Multipart) message.getContent();

                for (int k = 0; k < multiPart.getCount(); k++) {
                    MimeBodyPart part = (MimeBodyPart) multiPart.getBodyPart(k);
                    if (Part.ATTACHMENT.equalsIgnoreCase(part.getDisposition())) {
                        // return file input stream
                        return part.getInputStream();
                    }
                }
            }
        }
        receiveFolder.close(false);
        return null;
    }


}
