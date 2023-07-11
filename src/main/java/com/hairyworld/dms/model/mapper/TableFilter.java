package com.hairyworld.dms.model.mapper;

public enum TableFilter {
    NO_FILTER("No filtrar"),
    CLIENT_NAME("Nombre cliente"),
    DOG_NAME("Nombre perro"),
    PHONE("Telefono cliente"),
    MANTAINMENT("Mantenimiento"),
    NEXT_DATE("Proxima cita");

    private final String filter;

    TableFilter(String filter) {
        this.filter = filter;
    }

    public String getFilter() {
        return filter;
    }

    @Override
    public String toString() {
        return filter;
    }
}
