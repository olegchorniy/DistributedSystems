CREATE DATABASE fly_booking;
CREATE USER fly_admin WITH PASSWORD 'fly_password';
GRANT ALL PRIVILEGES ON DATABASE fly_booking TO fly_admin;

CREATE TABLE public.bookings (
  id          SERIAL PRIMARY KEY,
  client_name VARCHAR(20) NOT NULL,
  fly_number  VARCHAR(20) NOT NULL,
  "from"      VARCHAR(50) NOT NULL,
  "to"        VARCHAR(50) NOT NULL,
  "date"      DATE        NOT NULL
);

CREATE DATABASE hotel_booking;
CREATE USER hotel_admin WITH PASSWORD 'hotel_password';
GRANT ALL PRIVILEGES ON DATABASE hotel_booking TO hotel_admin;

CREATE TABLE public.bookings (
  id          SERIAL PRIMARY KEY,
  client_name VARCHAR(20) NOT NULL,
  hotel_name  VARCHAR(20) NOT NULL,
  arrival     DATE        NOT NULL,
  departure   DATE        NOT NULL
);