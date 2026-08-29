package com.openclassrooms.mddapi.comment.notification;

import com.openclassrooms.mddapi.system.config.MddProperties;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/** Sends an article-author email after a comment transaction has been committed. */
@Service
public class CommentNotificationService {

  private final JavaMailSender mailSender;
  private final MddProperties properties;

  public CommentNotificationService(JavaMailSender mailSender, MddProperties properties) {
    this.mailSender = mailSender;
    this.properties = properties;
  }

  /**
   * Sends the notification associated with a committed comment.
   *
   * @param event recipient address and post title
   */
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void notifyArticleAuthor(CommentCreatedEvent event) {
    SimpleMailMessage message = new SimpleMailMessage();
    message.setFrom(properties.getMail().getFrom());
    message.setTo(event.articleAuthorEmail());
    message.setSubject(properties.getMail().getCommentNotification().getSubject());
    message.setText(
        properties.getMail().getCommentNotification().getBody().formatted(event.postTitle()));
    mailSender.send(message);
  }
}
