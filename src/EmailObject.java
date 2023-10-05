import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import java.io.Serializable;
import java.util.Properties;

class EmailObject implements Serializable {
	String recipient_email;
	String subject;
	String content;
	String date;
	
	public EmailObject(String recipient_email, String subject, String content, String date) {
		this.recipient_email = recipient_email;
		this.subject = subject;
		this.content = content;
		this.date = date;
	}

    void mailSend() {

        final String username = "muddithauom@gmail.com";
        final String password = "qcsj erbn drud pruh";

        Properties prop = new Properties();
		prop.put("mail.smtp.host", "smtp.gmail.com");
        prop.put("mail.smtp.port", "587");
        prop.put("mail.smtp.auth", "true");
        prop.put("mail.smtp.starttls.enable", "true"); //TLS
        
        Session session = Session.getInstance(prop,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, password);
                    }
                });

        try {

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress("from@gmail.com"));
            message.setRecipients(
                    Message.RecipientType.TO,
                    InternetAddress.parse(this.recipient_email)
            );
            message.setSubject(this.subject);
            message.setText(this.content);

            Transport.send(message);

        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

}

