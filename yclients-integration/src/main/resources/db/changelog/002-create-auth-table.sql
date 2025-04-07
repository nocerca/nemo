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
