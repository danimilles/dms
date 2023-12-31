package com.hairyworld.dms.model.view;

public enum TableFilter {
    NO_FILTER("No filtrar"),
    CLIENT_NAME("Nombre cliente"),
    DNI("Dni"),
    DOG_NAME("Nombre mascota"),
    PHONE("Telefono cliente"),
    MANTAINMENT("Mantenimiento"),
    NEXT_DATE("Proxima cita"),
    DATE("Fecha"),
    AMOUNT("Cantidad"),
    OBSERVATIONS("Observaciones"),
    SERVICE("Servicio"),
    DESCRIPTION("Descripcion"),
    RACE("Raza");

    private final String filter;

    TableFilter(String filter) {
        this.filter = filter;
    }

    @Override
    public String toString() {
        return filter;
    }

}
