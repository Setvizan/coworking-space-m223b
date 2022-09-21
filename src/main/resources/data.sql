INSERT INTO MEMBER (id, firstname, lastname, email, password_hash, is_admin)
VALUES ('9135f12e-1b66-4ee6-bbae-df37303cc154','admin', 'user','admin@gmail.com', '$2a$10$aDD6I9Ej5.W8busvlsdPx.JvMWyJX8cOeOfVb.3q73KH2swww/N9C', true); -- password1234

INSERT INTO MEMBER (id, firstname, lastname, email, password_hash, is_admin)
VALUES ('5435f12e-1b12-4ee6-bbae-df34323cc154','member', 'user','member@gmail.com', '$2a$10$aDD6I9Ej5.W8busvlsdPx.JvMWyJX8cOeOfVb.3q73KH2swww/N9C', true); -- password1234

INSERT INTO BOOKING (id, date, status, booking_type, member_id)
VALUES ('e7e7070f-7dd2-4e57-bbd9-b0cbaac4e8f2', '2022-12-12', 'PENDING', 'DAY', '5435f12e-1b12-4ee6-bbae-df34323cc154');