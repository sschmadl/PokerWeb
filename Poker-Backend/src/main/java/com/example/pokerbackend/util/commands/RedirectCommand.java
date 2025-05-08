package com.example.pokerbackend.util.commands;

public class RedirectCommand {
    private String command = "redirect";
    private String route;

    public RedirectCommand(String route) {
        this.route = route;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public String getRoute() {
        return route;
    }

    public void setRoute(String route) {
        this.route = route;
    }
}
