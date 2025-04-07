INSERT INTO company (id, title, phone, country, allow_delete_record, allow_change_record, site)
VALUES
    (1001, 'Test Company 1', '+1234567890', 'Country A', TRUE, FALSE, 'https://testcompany1.com'),
    (1002, 'Test Company 2', '+1987654321', 'Country B', FALSE, TRUE, 'https://testcompany2.com');

INSERT INTO staff (id, name, specialization)
VALUES
    (2001, 'John Smith', 'Hairdresser'),
    (2002, 'Emily Davis', 'Manicurist');

INSERT INTO client (id, phone, name, surname, patronymic, email)
VALUES
    (3001, '+1234567890', 'Alice', 'Brown', 'Ivanovna', 'alice.brown@example.com'),
    (3002, '+1987654321', 'Bob', 'Green', 'Petrovich', 'bob.green@example.com');

INSERT INTO service (id, title, weight, seance_length)
VALUES
    (4001, 'Haircut', 1, 30),
    (4002, 'Manicure', 2, 45);

INSERT INTO record (
    id, company_id, staff_id, client_id, date, datetime, create_date, length, comment, deleted, notify_by_sms, notify_by_email, updated
)
VALUES
    (5001, 1001, 2001, 3001, '2025-04-05 10:00:00', '2025-04-05 10:00:00', '2025-04-04 15:00:00', 30, 'First record comment', FALSE, FALSE, FALSE, '2025-04-04 15:30:00'),
    (5002, 1002, 2002, 3002, '2025-04-06 11:00:00', '2025-04-06 11:00:00', '2025-04-04 16:00:00', 45, 'Second record comment', FALSE, FALSE, FALSE, '2025-04-04 16:30:00');

INSERT INTO record_service (record_id, service_id)
VALUES
    (5001, 4001),
    (5002, 4002);

INSERT INTO auth (
    user_token, name, phone, login, email, password, avatar, is_approved, company_id, partner_token
)
VALUES
    ('token1', 'Admin One', '+1234567890', 'admin1', 'admin1@example.com', 'password123', NULL, TRUE, 1001, 'partner-token-1'),
    ('token2', 'Admin Two', '+1987654321', 'admin2', 'admin2@example.com', 'password456', NULL, FALSE, 1002, 'partner-token-2');
