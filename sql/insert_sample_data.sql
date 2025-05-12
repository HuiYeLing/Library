INSERT INTO users (username, password, email) VALUES
('user1', 'password1', 'user1@example.com'),
('user2', 'password2', 'user2@example.com'),
('user3', 'password3', 'user3@example.com');

INSERT INTO seats (seat_number, location, status) VALUES
('A1', 'First Floor', 'AVAILABLE'),
('A2', 'First Floor', 'OCCUPIED'),
('A3', 'Second Floor', 'MAINTENANCE');

INSERT INTO reservations (seat_id, username, start_time, end_time, status) VALUES
(1, 'user1', '2023-10-01 10:00:00', '2023-10-01 11:00:00', 'ACTIVE'),
(2, 'user2', '2023-10-02 12:00:00', '2023-10-02 13:00:00', 'COMPLETED'),
(3, 'user3', '2023-10-03 14:00:00', '2023-10-03 15:00:00', 'CANCELLED');