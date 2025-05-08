package com.example.pokerbackend.util.commands;

public class ServerMessageCommand {
    private String command = "server-message";
    private String title;
    private String message;
    private String color = "green";

    public ServerMessageCommand(String title, String message) {
        this.title = title;
        this.message = message;
    }

    public ServerMessageCommand(String title, String message, String color) {
        this(title, message);
        this.color = color;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
