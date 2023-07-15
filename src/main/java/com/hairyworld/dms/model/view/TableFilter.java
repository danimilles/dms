package com.hairyworld.dms.model.view;

public enum TableFilter {
    NO_FILTER("No filtrar"),
    CLIENT_NAME("Nombre cliente"),
    DNI("Dni"),
    DOG_NAME("Nombre mascota"),
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
