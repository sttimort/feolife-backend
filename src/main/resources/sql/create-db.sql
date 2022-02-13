create type permission as enum ('LOGIN');

CREATE SEQUENCE IF NOT EXISTS user_profile_id_sequence
    AS bigint
    INCREMENT 1
    MINVALUE 1
    START 10001;

create table user_profile
(
    id          bigint primary key default nextval('user_profile_id_sequence'),
    uuid        uuid         not null unique,
    first_name  varchar(128) not null,
    last_name   varchar(128) not null,
    middle_name varchar(128)       default null
);

CREATE SEQUENCE IF NOT EXISTS username_password_credentials_id_sequence
    AS bigint
    INCREMENT 1
    MINVALUE 1
    START 10001;

create table username_password_credentials
(
    id              bigint primary key DEFAULT nextval('username_password_credentials_id_sequence'),
    username        varchar(128)                   not null unique,
    password        text                           not null,
    user_profile_id bigint references user_profile not null
);

create table role
(
    id   bigint primary key,
    name varchar(128) not null unique
);

create table user_role
(
    user_profile_id bigint references user_profile not null,
    role_id         bigint references role         not null,
    primary key (user_profile_id, role_id)
);

create table role_permission
(
    role_id    bigint references role not null,
    permission permission             not null,
    primary key (role_id, permission)
);
