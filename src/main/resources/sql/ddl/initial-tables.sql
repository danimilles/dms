create table main.client
(
    name         TEXT    not null,
    phone        TEXT not null,
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
    maintainment         TEXT,
    race         TEXT    not null,
    observations TEXT,
    image BLOB
);

create table main.clientdog
(
    iddog    INTEGER not null
        constraint clientdog_dog_id_fk
            references dog on delete cascade,
    idclient integer not null
        constraint clientdog_client_id_fk
            references client on delete cascade,
    constraint clientdog_pk
        primary key (idclient, iddog)
);

create table date
(
    id          integer not null
        constraint date_pk
            primary key autoincrement,
    datetimestart    TEXT    not null,
    datetimeend    TEXT    not null,
    iddog     integer
        constraint date_dog_id_fk
            references dog on delete set null,
    description TEXT,
    idservice  integer constraint date_service_id_fk
        references service on delete set null,
    idclient    integer not null constraint date_client_id_fk
        references client on delete cascade
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
            references service on delete set null,
    idclient  integer
        constraint payment_client_id_fk
            references client on delete set null,
    description TEXT,
    amount double not null,
    datetime TEXT default current_timestamp not null
);