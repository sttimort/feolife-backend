CREATE SEQUENCE IF NOT EXISTS billing_account_id_sequence AS bigint INCREMENT 1 MINVALUE 1 START 10001;
CREATE TABLE IF NOT EXISTS billing_account
(
    id                    bigint PRIMARY KEY default nextval('billing_account_id_sequence'),
    uuid                  uuid           NOT NULL,
    balance               numeric(20, 2) NOT NULL,
    creation_datetime     timestamptz    NOT NULL,
    modification_datetime timestamptz    NOT NULL
);

CREATE SEQUENCE IF NOT EXISTS user_profile_id_sequence AS bigint INCREMENT 1 MINVALUE 1 START 10001;
CREATE TABLE IF NOT EXISTS user_profile
(
    id                 bigint primary key default nextval('user_profile_id_sequence'),
    uuid               uuid         not null unique,
    type               varchar(128) NOT NULL,
    billing_account_id bigint REFERENCES billing_account,
    first_name         varchar(128) not null,
    last_name          varchar(128) not null,
    middle_name        varchar(128),
    gender             varchar(128),
    birth_date         date
);

CREATE SEQUENCE IF NOT EXISTS username_password_credentials_id_sequence AS bigint INCREMENT 1 MINVALUE 1 START 10001;
create table username_password_credentials
(
    id              bigint primary key DEFAULT nextval('username_password_credentials_id_sequence'),
    username        varchar(128)                   not null unique,
    password        text                           not null,
    user_profile_id bigint references user_profile not null
);

CREATE SEQUENCE IF NOT EXISTS role_id_sequence AS bigint INCREMENT 1 MINVALUE 1 START 10001;
CREATE TABLE IF NOT EXISTS role
(
    id                              bigint PRIMARY KEY    default nextval('role_id_sequence'),
    name                            varchar(128) NOT NULL UNIQUE,
    is_assigned_on_profile_creation bool         NOT NULL DEFAULT false
);

CREATE TABLE IF NOT EXISTS user_role
(
    user_profile_id bigint REFERENCES user_profile NOT NULL,
    role_id         bigint REFERENCES role         NOT NULL,
    PRIMARY KEY (user_profile_id, role_id)
);

CREATE TABLE IF NOT EXISTS role_permission
(
    role_id    bigint REFERENCES role NOT NULL,
    permission varchar(128)           NOT NULL,
    primary key (role_id, permission)
);

CREATE TABLE IF NOT EXISTS living_address
(
    id              bigint PRIMARY KEY,
    uuid            uuid         NOT NULL UNIQUE,
    user_profile_id bigint       NOT NULL REFERENCES user_profile,
    city            varchar(128) NOT NULL,
    street          varchar(128) NOT NULL,
    building        varchar(128) NOT NULL
);

CREATE SEQUENCE IF NOT EXISTS relocation_request_id_sequence
    AS bigint
    INCREMENT 1
    MINVALUE 1
    START 10001;

CREATE TABLE IF NOT EXISTS relocation_request
(
    id                    bigint PRIMARY KEY default nextval('relocation_request_id_sequence'),
    uuid                  uuid         NOT NULL UNIQUE,
    requester_id          bigint       NOT NULL REFERENCES user_profile,
    relocation_datetime   timestamptz  NOT NULL,
    destination_city      varchar(128) NOT NULL,
    destination_street    varchar(128) NOT NULL,
    destination_building  varchar(64)  NOT NULL,
    status                varchar(128) NOT NULL,
    details               text,
    creation_datetime     timestamptz  NOT NULL,
    modification_datetime timestamptz  NOT NULL
);

CREATE SEQUENCE IF NOT EXISTS loan_request_id_sequence
    AS bigint
    INCREMENT 1
    MINVALUE 1
    START 10001;

CREATE TABLE IF NOT EXISTS loan_request
(
    id                        bigint PRIMARY KEY default nextval('loan_request_id_sequence'),
    uuid                      uuid           NOT NULL UNIQUE,
    requester_id              bigint         NOT NULL REFERENCES user_profile,
    type                      varchar(128)   NOT NULL,
    purpose                   text           NOT NULL,
    requested_amount          numeric(14, 2) NOT NULL,
    return_in_months          int            NOT NULL,
    declared_job_position     text,
    declared_monthly_income   numeric(14, 2),
    surety                    text,
    agreed_to_life_insurance  bool,
    guarantor_id              bigint REFERENCES user_profile,
    declared_guarantor_status text,
    details                   text,
    creation_datetime         timestamptz    NOT NULL,
    modification_datetime     timestamptz    NOT NULL
);

CREATE SEQUENCE IF NOT EXISTS visit_request_id_sequence
    AS bigint
    INCREMENT 1
    MINVALUE 1
    START 10001;

CREATE TABLE IF NOT EXISTS visit_request
(
    id                       bigint PRIMARY KEY default nextval('visit_request_id_sequence'),
    uuid                     uuid         NOT NULL UNIQUE,
    visitor_id               bigint       NOT NULL REFERENCES user_profile,
    visitee_id               bigint       NOT NULL REFERENCES user_profile,
    arrival_interval_start   timestamptz  NOT NULL,
    arrival_interval_end     timestamptz,
    departure_interval_start timestamptz,
    departure_interval_end   timestamptz,
    details                  text,
    status                   varchar(128) NOT NULL,
    creation_datetime        timestamptz  NOT NULL,
    modification_datetime    timestamptz  NOT NULL
);

CREATE SEQUENCE IF NOT EXISTS arrest_id_sequence
    AS bigint
    INCREMENT 1
    MINVALUE 1
    START 10001;

CREATE TABLE IF NOT EXISTS arrest
(
    id                    bigint PRIMARY KEY default nextval('arrest_id_sequence'),
    uuid                  uuid        NOT NULL UNIQUE,
    arrestor_id           bigint      NOT NULL REFERENCES user_profile,
    arrestee_id           bigint      NOT NULL REFERENCES user_profile,
    reason                text        NOT NULL,
    arrest_start_datetime timestamptz NOT NULL,
    arrest_end_datetime   timestamptz NOT NULL,
    creation_datetime     timestamptz NOT NULL,
    modification_datetime timestamptz NOT NULL
);

CREATE SEQUENCE IF NOT EXISTS souls_selling_offer_id_sequence
    AS bigint
    INCREMENT 1
    MINVALUE 1
    START 10001;

CREATE TABLE IF NOT EXISTS souls_selling_offer
(
    id                    bigint PRIMARY KEY default nextval('souls_selling_offer'),
    seller_id             bigint         NOT NULL REFERENCES user_profile,
    quantity              int            NOT NULL,
    price                 numeric(10, 2) NOT NULL,
    creation_datetime     timestamptz    NOT NULL,
    modification_datetime timestamptz    NOT NULL
);

CREATE SEQUENCE IF NOT EXISTS souls_purchase_offer_id_sequence
    AS bigint
    INCREMENT 1
    MINVALUE 1
    START 10001;

CREATE TABLE IF NOT EXISTS souls_purchase_offer
(
    id                    bigint PRIMARY KEY default nextval('souls_purchase_offer_id_sequence'),
    creation_datetime     timestamptz NOT NULL,
    modification_datetime timestamptz NOT NULL
)
