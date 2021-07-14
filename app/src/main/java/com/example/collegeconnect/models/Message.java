package com.example.collegeconnect.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

@ParseClassName("Message")
public class Message extends ParseObject {
    public Message() {}

    public static final String KEY_SENDER = "sender";
    public static final String KEY_BODY = "body";
    public static final String KEY_CONVERSATION = "conversation";

    public User getSender() {
        return (User) getParseUser(KEY_SENDER);
    }

    public String getBody() {
        return getString(KEY_BODY);
    }

    public Conversation getConversation() {
        return (Conversation) getParseObject(KEY_CONVERSATION);
    }

    public void setSender(ParseUser user) {
        put(KEY_SENDER, user);
    }

    public void setBody(String body) {
        put(KEY_BODY, body);
    }

    public void setConversation(Conversation conversation) {
        put(KEY_CONVERSATION, conversation);
    }
}
