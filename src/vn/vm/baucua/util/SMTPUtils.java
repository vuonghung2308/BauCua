package vn.vm.baucua.util;

import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class SMTPUtils {

    private static final String MAIL_FROME = "usernetwork.service123@gmail.com";
    private static final String PASSWORD = "servicenetwork123";
    private static final String HOST = "smtp.gmail.com";
    private static final String SUBJECT = "Đặt lại mật khẩu tài khoản Bầu cua";

    public static void sendMailToUser(String email, String key) {
        Properties properties = System.getProperties();

        properties.put("mail.smtp.host", HOST);
        properties.put("mail.smtp.port", "465");
        properties.put("mail.smtp.socketFactory.class",
                "javax.net.ssl.SSLSocketFactory");
        properties.put("mail.smtp.auth", "true");

        Session session = Session.getInstance(properties, new javax.mail.Authenticator() {
            @Override
            protected javax.mail.PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(MAIL_FROME, PASSWORD);
            }
        });

        session.setDebug(false);

        try {
            // Create a default MimeMessage object.
            MimeMessage message = new MimeMessage(session);

            // Set From: header field of the header.
            message.setFrom(new InternetAddress(MAIL_FROME));

            // Set To: header field of the header.
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(email));

            // Set Subject: header field
            message.setSubject(SUBJECT);

            String mess = "Mã xác nhận của bạn là: " + key + ". Mã xác nhận của bạn sẽ có hiệu lực trong 5 phút";
            // Now set the actual message
            message.setText(mess);

            // Send message
            Transport.send(message);
        } catch (MessagingException mex) {
            System.out.println(mex.getMessage());
        }
    }
}
