DROP TABLE IF EXISTS MEMBER CASCADE;
CREATE TABLE MEMBER
(
    id            UUID,
    firstname     VARCHAR(2000) NOT NULL,
    lastname      VARCHAR(2000) NOT NULL,
    email         VARCHAR(2000) NOT NULL UNIQUE,
    password_hash VARCHAR(2000) NOT NULL,
    is_admin      BOOLEAN       NOT NULL DEFAULT FALSE,

    PRIMARY KEY (id)
);

DROP TABLE IF EXISTS BOOKING CASCADE;
CREATE TABLE BOOKING
(
    id UUID,
    date DATE NOT NULL,
    status ENUM('APPROVED', 'DECLINED', 'PENDING') NOT NULL,
    booking_type ENUM('MORNING', 'AFTERNOON', 'DAY') NOT NULL,
    user_id UUID NOT NULL,

    PRIMARY KEY (id),
    FOREIGN KEY (user_id) REFERENCES MEMBER (id)
);