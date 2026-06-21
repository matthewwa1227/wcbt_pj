package com.casualapp.android.model;

public class Coordinator extends User {
    public Coordinator() {
        super();

    }

    public Coordinator(Long id, String phoneNumber, String name, String createAt) {
        super(id, phoneNumber, name, Role.COORDINATOR, createAt);
    }
}

