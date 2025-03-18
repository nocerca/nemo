CREATE TABLE company (
                         companyInnerId BIGINT PRIMARY KEY,
                         id BIGINT UNIQUE NOT NULL,
                         title VARCHAR(255) NOT NULL,
                         phone VARCHAR(20),
                         country VARCHAR(100),
                         allow_delete_record BOOLEAN NOT NULL,
                         allow_change_record BOOLEAN NOT NULL,
                         site VARCHAR(255)
);

CREATE TABLE staff (
                       staffInnerId BIGINT PRIMARY KEY,
                       id BIGINT UNIQUE NOT NULL,
                       name VARCHAR(255) NOT NULL
);

CREATE TABLE client (
                        clientInnerId BIGINT PRIMARY KEY,
                        id BIGINT UNIQUE NOT NULL,
                        phone VARCHAR(20) UNIQUE NOT NULL,
                        name VARCHAR(255),
                        surname VARCHAR(255),
                        patronymic VARCHAR(255),
                        email VARCHAR(255) UNIQUE NOT NULL
);

CREATE TABLE service (
                         serviceInnerId BIGINT PRIMARY KEY,
                         id BIGINT UNIQUE NOT NULL,
                         title VARCHAR(255) NOT NULL,
                         weight INT NOT NULL,
                         seance_length INT NOT NULL
);

CREATE TABLE record (
                        recordInnerId BIGINT PRIMARY KEY,
                        id BIGINT UNIQUE NOT NULL,
                        companyInnerId BIGINT NOT NULL,
                        staffInnerId BIGINT NOT NULL,
                        clientInnerId BIGINT NOT NULL,
                        date TIMESTAMP NOT NULL,
                        datetime TIMESTAMP NOT NULL,
                        create_date TIMESTAMP NOT NULL,
                        length INT NOT NULL,
                        comment TEXT,
                        deleted BOOLEAN NOT NULL DEFAULT FALSE,
                        FOREIGN KEY (companyInnerId) REFERENCES company(companyInnerId),
                        FOREIGN KEY (staffInnerId) REFERENCES staff(staffInnerId),
                        FOREIGN KEY (clientInnerId) REFERENCES client(clientInnerId)
);

CREATE TABLE record_service (
                                record_serviceInnerId BIGINT PRIMARY KEY,
                                recordInnerId BIGINT NOT NULL,
                                serviceInnerId BIGINT NOT NULL,
                                FOREIGN KEY (recordInnerId) REFERENCES record(recordInnerId) ON DELETE CASCADE,
                                FOREIGN KEY (serviceInnerId) REFERENCES service(serviceInnerId) ON DELETE CASCADE
);
