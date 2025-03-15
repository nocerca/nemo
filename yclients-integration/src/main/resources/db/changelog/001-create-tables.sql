CREATE TABLE company (
                         id BIGINT PRIMARY KEY,
                         title VARCHAR(255) NOT NULL,
                         phone VARCHAR(20),
                         country VARCHAR(100),
                         allow_delete_record BOOLEAN NOT NULL,
                         allow_change_record BOOLEAN NOT NULL,
                         site VARCHAR(255)
);

CREATE TABLE staff (
                       id BIGINT PRIMARY KEY,
                       name VARCHAR(255) NOT NULL
);

CREATE TABLE client (
                        id BIGINT PRIMARY KEY,
                        phone VARCHAR(20) UNIQUE NOT NULL,
                        name VARCHAR(255),
                        surname VARCHAR(255),
                        patronymic VARCHAR(255),
                        email VARCHAR(255) UNIQUE NOT NULL
);

CREATE TABLE service (
                         id BIGINT PRIMARY KEY,
                         title VARCHAR(255) NOT NULL,
                         weight INT NOT NULL,
                         seance_length INT NOT NULL
);

CREATE TABLE record (
                        id BIGINT PRIMARY KEY,
                        company_id BIGINT NOT NULL,
                        staff_id BIGINT NOT NULL,
                        client_id BIGINT NOT NULL,
                        date TIMESTAMP NOT NULL,
                        datetime TIMESTAMP NOT NULL,
                        create_date TIMESTAMP NOT NULL,
                        length INT NOT NULL,
                        comment TEXT,
                        deleted BOOLEAN NOT NULL DEFAULT FALSE,
                        FOREIGN KEY (company_id) REFERENCES company(id),
                        FOREIGN KEY (staff_id) REFERENCES staff(id),
                        FOREIGN KEY (client_id) REFERENCES client(id)
);

CREATE TABLE record_service (
                                record_id BIGINT NOT NULL,
                                service_id BIGINT NOT NULL,
                                PRIMARY KEY (record_id, service_id),
                                FOREIGN KEY (record_id) REFERENCES record(id) ON DELETE CASCADE,
                                FOREIGN KEY (service_id) REFERENCES service(id) ON DELETE CASCADE
);
