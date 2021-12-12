/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package smtp;

import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import vn.vm.baucua.util.KeyForgotUtil;

/**
 *
 * @author User
 */
public class SMTP {
    private static String MAIL_FROME =  "usernetwork.service123@gmail.com";
    private static String PASSWORD = "servicenetwork123";
    private static String HOST = "smtp.gmail.com";
    private static String SUBJECT = "Support Change Password";
    
    
    public static void sendMailToUser(String email, String key){
       // Get system properties
       Properties properties = System.getProperties();

       // Setup mail server
       properties.put("mail.smtp.host", HOST);
       properties.put("mail.smtp.port", "465");
       properties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
       properties.put("mail.smtp.auth", "true");

       Session session = Session.getInstance(properties, new javax.mail.Authenticator() {
           protected javax.mail.PasswordAuthentication getPasswordAuthentication() {
               return new PasswordAuthentication(MAIL_FROME, PASSWORD);
           }
       });

       // Used to debug SMTP issues
       session.setDebug(true);

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
           mex.printStackTrace();
       }
    }
}
