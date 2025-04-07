CREATE TABLE company (
                         company_inner_id BIGSERIAL PRIMARY KEY,
                         id BIGINT UNIQUE NOT NULL,
                         title VARCHAR(255) NOT NULL,
                         phone VARCHAR(20),
                         country VARCHAR(100),
                         allow_delete_record BOOLEAN NOT NULL,
                         allow_change_record BOOLEAN NOT NULL,
                         site VARCHAR(255)
);

CREATE TABLE staff (
                       staff_inner_id BIGSERIAL PRIMARY KEY,
                       id BIGINT UNIQUE NOT NULL,
                       name VARCHAR(255) NOT NULL,
                       specialization VARCHAR(255)
);

CREATE TABLE client (
                        client_inner_id BIGSERIAL PRIMARY KEY,
                        id BIGINT UNIQUE NOT NULL,
                        phone VARCHAR(20) UNIQUE NOT NULL,
                        name VARCHAR(255),
                        surname VARCHAR(255),
                        patronymic VARCHAR(255),
                        email VARCHAR(255) UNIQUE NOT NULL
);

CREATE TABLE service (
                         service_inner_id BIGSERIAL PRIMARY KEY,
                         id BIGINT UNIQUE NOT NULL,
                         title VARCHAR(255) NOT NULL,
                         weight INT NOT NULL,
                         seance_length INT NOT NULL
);

CREATE TABLE record (
                        record_inner_id BIGSERIAL PRIMARY KEY,
                        id BIGINT UNIQUE NOT NULL,

                        company_id BIGINT NOT NULL,
                        staff_id BIGINT NOT NULL,
                        client_id BIGINT NOT NULL,

                        date TIMESTAMP NOT NULL,
                        datetime TIMESTAMP NOT NULL,
                        create_date TIMESTAMP NOT NULL,
                        length INT NOT NULL,
                        comment TEXT,
                        deleted BOOLEAN NOT NULL DEFAULT FALSE,
                        notify_by_sms BOOLEAN NOT NULL DEFAULT FALSE,
                        notify_by_email BOOLEAN NOT NULL DEFAULT FALSE,
                        updated TIMESTAMP,

                        FOREIGN KEY (company_id) REFERENCES company(id) ON DELETE CASCADE,
                        FOREIGN KEY (staff_id) REFERENCES staff(id) ON DELETE CASCADE,
                        FOREIGN KEY (client_id) REFERENCES client(id) ON DELETE CASCADE
);

CREATE TABLE record_service (
                                record_service_inner_id BIGSERIAL PRIMARY KEY,
                                record_id BIGINT NOT NULL,
                                service_id BIGINT NOT NULL,

                                FOREIGN KEY (record_id) REFERENCES record(id) ON DELETE CASCADE,
                                FOREIGN KEY (service_id) REFERENCES service(id) ON DELETE CASCADE
);

CREATE TABLE auth (
                      id BIGSERIAL PRIMARY KEY,
                      user_token VARCHAR(255) NOT NULL,
                      name VARCHAR(255),
                      phone VARCHAR(20),
                      login VARCHAR(255) NOT NULL,
                      email VARCHAR(255),
                      password VARCHAR(255) NOT NULL,
                      avatar VARCHAR(255),
                      is_approved BOOLEAN NOT NULL,
                      company_id BIGINT,
                      partner_token VARCHAR(255)
);
