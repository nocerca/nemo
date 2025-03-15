CREATE TABLE auth (
                      id BIGINT PRIMARY KEY,
                      user_token VARCHAR(255) NOT NULL,
                      name VARCHAR(255),
                      phone VARCHAR(20),
                      login VARCHAR(255),
                      email VARCHAR(255),
                      avatar VARCHAR(255),
                      is_approved BOOLEAN NOT NULL
);
