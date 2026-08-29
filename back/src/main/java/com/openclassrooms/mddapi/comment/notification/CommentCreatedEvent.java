package com.openclassrooms.mddapi.comment.notification;

/**
 * Event published when a comment is created and an article-author notification is required.
 *
 * @param articleAuthorEmail recipient email address
 * @param postTitle title of the commented post
 */
public record CommentCreatedEvent(String articleAuthorEmail, String postTitle) {}
