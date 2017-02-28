CREATE TABLE hotel_bookings (
  id          INT PRIMARY KEY AUTO_INCREMENT,
  client_name VARCHAR(20) NOT NULL,
  hotel_name  VARCHAR(20) NOT NULL,
  arrival     DATE        NOT NULL,
  departure   DATE        NOT NULL
);