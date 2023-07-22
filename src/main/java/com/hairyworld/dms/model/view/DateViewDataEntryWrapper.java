package com.hairyworld.dms.model.view;

import com.calendarfx.model.Interval;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.joda.time.DateTime;

import java.time.Instant;
import java.time.ZoneOffset;

@AllArgsConstructor
@Data
@Builder
public class DateViewDataEntryWrapper {
    private SimpleObjectProperty<Long> id;
    private SimpleObjectProperty<Interval> interval;
    private SimpleStringProperty description;

    private SimpleObjectProperty<DogViewData> dog;
    private SimpleObjectProperty<ClientViewData> client;
    private SimpleObjectProperty<ServiceViewData> service;

    public DateViewDataEntryWrapper() {
        this.id = new SimpleObjectProperty<>();
        this.interval = new SimpleObjectProperty<>();
        this.description = new SimpleStringProperty();
        this.dog = new SimpleObjectProperty<>();
        this.client = new SimpleObjectProperty<>();
        this.service = new SimpleObjectProperty<>();
    }

    public DateViewData toDateViewData() {
        return DateViewData.builder()
                .id(id.get())
                .datetimestart(new DateTime(interval.get().getStartDateTime().toInstant(ZoneOffset.UTC).toEpochMilli()))
                .datetimeend(new DateTime(interval.get().getEndDateTime().toInstant(ZoneOffset.UTC).toEpochMilli()))
                .description(description.get())
                .dog(dog.get())
                .client(client.get())
                .service(service.get())
                .build();
    }

    public DateViewDataEntryWrapper(final DateViewData date) {
        this.id = new SimpleObjectProperty<>(date.getId());
        this.interval = new SimpleObjectProperty<>(new Interval(Instant.ofEpochMilli(date.getDatetimestart().getMillis()), Instant.ofEpochMilli(date.getDatetimeend().getMillis()), ZoneOffset.UTC));
        this.description = new SimpleStringProperty(date.getDescription());
        this.dog = new SimpleObjectProperty<>(date.getDog());
        this.client = new SimpleObjectProperty<>(date.getClient());
        this.service = new SimpleObjectProperty<>(date.getService());
    }

    public String getEntryTile() {
        final String client = this.getClient().get() != null && this.getClient().get().getName() != null ?
                this.getClient().get().getName()  : "";
        final String dog = this.getDog().get() != null && this.getDog().get().getName() != null ?
                this.getDog().get().getName() : "";
        final String description = this.getDescription() != null ? this.getDescription().get() : "";
        final String service = this.getService().get() != null
                && this.getService().get().getDescription() != null ? " - " + this.getService().get().getDescription() : "";

        return new StringBuilder()
                .append(client)
                .append(!client.isEmpty() && !dog.isEmpty() ? " - " : "")
                .append(dog)
                .append(!client.isEmpty() || !dog.isEmpty() ? "\n" : "")
                .append(description)
                .append(service)
                .toString();
    }
}