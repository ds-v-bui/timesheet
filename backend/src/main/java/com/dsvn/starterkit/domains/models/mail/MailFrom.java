package com.dsvn.starterkit.domains.models.mail;

public enum MailFrom {
    NO_REPLY("cuong2011.94@gmail.com", "test"),
    SUPPORT("cuong2011.94@gmail.com", "test");

    private final String email;
    private final String name;

    private MailFrom(String email, String name) {
        this.email = email;
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }
}
