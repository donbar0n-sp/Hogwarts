CREATE TABLE car (
    id SERIAL PRIMARY KEY,
    brand VARCHAR(100) NOT NULL,
    model VARCHAR(100) NOT NULL,
    price NUMERIC(10, 2) NOT NULL
);

CREATE TABLE person (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    age INT CHECK (age >= 0),
    has_license BOOLEAN NOT NULL DEFAULT FALSE,
    car_id INT REFERENCES car(id)
);