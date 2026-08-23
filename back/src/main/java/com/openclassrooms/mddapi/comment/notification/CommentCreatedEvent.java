package com.openclassrooms.mddapi.comment.notification;

public record CommentCreatedEvent(String articleAuthorEmail, String postTitle) {}
