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
            references main.dog
            on delete cascade,
    idclient integer not null
        constraint clientdog_client_id_fk
            references main.client
            on delete cascade,
    constraint clientdog_pk
        primary key (idclient, iddog)
);

create table main.date
(
    id          integer not null
        constraint date_pk
            primary key autoincrement,
    datetime    TEXT    not null,
    idperro     integer not null
        constraint date_dog_id_fk
            references main.dog,
    description TEXT,
    idservicio  integer
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
            references main.service,
    idclient  integer                        not null
        constraint payment_client_id_fk
            references main.client,
    timestamp TEXT default current_timestamp not null
);