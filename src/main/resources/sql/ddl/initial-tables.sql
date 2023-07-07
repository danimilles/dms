create table main.client
(
    name         TEXT    not null,
    dni          TEXT,
    phone        INTEGER not null,
    observations TEXT,
    id           integer not null
        constraint client_pk
            primary key autoincrement
);

create table main.dog
(
    id           integer not null
        constraint dog_pk
            primary key autoincrement,
    name         TEXT    not null,
    race         TEXT    not null,
    observations TEXT
);

create table main.clientdog
(
    iddog    INTEGER not null
        constraint clientdog_dog_id_fk
            references dog
            on delete cascade,
    idclient integer not null
        constraint clientdog_client_id_fk
            references client
            on delete cascade,
    constraint clientdog_pk
        primary key (idclient, iddog)
);

create table date
(
    id          integer not null
        constraint date_pk
            primary key autoincrement,
    datetime    TEXT    not null,
    idperro     integer
        constraint date_dog_id_fk
            references dog,
    description TEXT,
    idservicio  integer,
    idclient    integer not null constraint date_client_id_fk
        references client
);

create table main.service
(
    description TEXT    not null
        constraint service_pk2
            unique,
    id          integer not null
        constraint service_pk
            primary key autoincrement
);

create table main.payment
(
    id        integer                        not null
        constraint payment_pk
            primary key autoincrement,
    idservice integer
        constraint payment_service_id_fk
            references service,
    idclient  integer                        not null
        constraint payment_client_id_fk
            references client,
    timestamp TEXT default current_timestamp not null
);

alter table main.payment
    add description TEXT;
