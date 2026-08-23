package com.openclassrooms.mddapi.comment.notification;

import com.openclassrooms.mddapi.system.config.MddProperties;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Service
public class CommentNotificationService {

  private final JavaMailSender mailSender;
  private final MddProperties properties;

  public CommentNotificationService(JavaMailSender mailSender, MddProperties properties) {
    this.mailSender = mailSender;
    this.properties = properties;
  }

  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void notifyArticleAuthor(CommentCreatedEvent event) {
    SimpleMailMessage message = new SimpleMailMessage();
    message.setFrom(properties.getMail().getFrom());
    message.setTo(event.articleAuthorEmail());
    message.setSubject("Nouveau commentaire sur votre article");
    message.setText(
        """
        Un nouveau commentaire a été publié sur votre article « %s ».

        MDD
        """
            .formatted(event.postTitle()));
    mailSender.send(message);
  }
}
