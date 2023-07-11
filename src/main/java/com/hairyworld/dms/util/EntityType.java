package com.hairyworld.dms.util;

public enum EntityType {
    CLIENT(1),
    DOG(2),
    DATE(3),
    PAYMENT(4),
    SERVICE(5);

    private final int id;

    EntityType(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}
