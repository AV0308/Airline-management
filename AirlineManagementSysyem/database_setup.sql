-- ============================================================
--  Airline Management System — Database Setup Script
--  Run this once in MySQL Workbench or the MySQL CLI:
--    mysql -u root -p < database_setup.sql
-- ============================================================

CREATE DATABASE IF NOT EXISTS airlinedb;
USE airlinedb;

-- ── Drop existing tables (safe re-run) ────────────────────
DROP TABLE IF EXISTS cancellation;
DROP TABLE IF EXISTS payment;
DROP TABLE IF EXISTS reservation;
DROP TABLE IF EXISTS passenger;
DROP TABLE IF EXISTS sector;
DROP TABLE IF EXISTS flight;
DROP TABLE IF EXISTS login;

-- ── login ─────────────────────────────────────────────────
CREATE TABLE login (
    username    VARCHAR(20)  NOT NULL PRIMARY KEY,
    password    VARCHAR(20)  NOT NULL
);

-- ── flight ────────────────────────────────────────────────
CREATE TABLE flight (
    f_code      VARCHAR(10)  NOT NULL PRIMARY KEY,
    f_name      VARCHAR(20)  NOT NULL,
    src         VARCHAR(30)  NOT NULL,
    dst         VARCHAR(30)  NOT NULL
);

-- ── sector ────────────────────────────────────────────────
CREATE TABLE sector (
    flight_code VARCHAR(20)  NOT NULL,
    capacity    VARCHAR(10)  NOT NULL,
    class_code  VARCHAR(5)   NOT NULL,
    class_name  VARCHAR(20)  NOT NULL,
    FOREIGN KEY (flight_code) REFERENCES flight(f_code) ON DELETE CASCADE
);

-- ── passenger ─────────────────────────────────────────────
CREATE TABLE passenger (
    pnr_no      VARCHAR(10)  NOT NULL PRIMARY KEY,
    name        VARCHAR(20)  NOT NULL,
    address     VARCHAR(30)  NOT NULL,
    nationality VARCHAR(15)  NOT NULL,
    gender      VARCHAR(10)  NOT NULL,
    ph_no       VARCHAR(15)  NOT NULL,
    passport_no VARCHAR(20)  NOT NULL,
    fl_code     VARCHAR(10)  NOT NULL,
    FOREIGN KEY (fl_code) REFERENCES flight(f_code)
);

-- ── reservation ───────────────────────────────────────────
CREATE TABLE reservation (
    pnr_no      VARCHAR(10)  NOT NULL,
    ticket_id   VARCHAR(10)  NOT NULL PRIMARY KEY,
    f_code      VARCHAR(10)  NOT NULL,
    jny_date    DATE         NOT NULL,
    jny_time    VARCHAR(10)  NOT NULL,
    src         VARCHAR(20)  NOT NULL,
    dst         VARCHAR(20)  NOT NULL,
    FOREIGN KEY (pnr_no) REFERENCES passenger(pnr_no),
    FOREIGN KEY (f_code) REFERENCES flight(f_code)
);

-- ── payment ───────────────────────────────────────────────
CREATE TABLE payment (
    pnr_no      VARCHAR(10)  NOT NULL,
    ph_no       VARCHAR(15)  NOT NULL,
    cheque_no   VARCHAR(15),
    card_no     VARCHAR(20),
    paid_amt    VARCHAR(10)  NOT NULL,
    pay_date    DATE         NOT NULL,
    FOREIGN KEY (pnr_no) REFERENCES passenger(pnr_no)
);

-- ── cancellation ──────────────────────────────────────────
CREATE TABLE cancellation (
    pnr_no            VARCHAR(10)  NOT NULL,
    cancellation_no   VARCHAR(10)  NOT NULL PRIMARY KEY,
    cancellation_date DATE         NOT NULL,
    fli_code          VARCHAR(15)  NOT NULL
);

-- ── Seed data ─────────────────────────────────────────────
INSERT INTO login VALUES ('admin', 'admin123');

INSERT INTO flight VALUES
    ('AI101', 'Air India Express',   'Delhi',     'Mumbai'),
    ('AI202', 'Air India Premium',   'Mumbai',    'Bangalore'),
    ('AI303', 'Air India Connect',   'Kolkata',   'Chennai'),
    ('AI404', 'Air India Regional',  'Hyderabad', 'Pune');

INSERT INTO sector VALUES
    ('AI101', '180', 'EC', 'Economy'),
    ('AI101', '20',  'BC', 'Business'),
    ('AI202', '160', 'EC', 'Economy'),
    ('AI202', '30',  'BC', 'Business');

SELECT 'Database setup complete.' AS Status;
