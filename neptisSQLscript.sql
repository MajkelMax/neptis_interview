DROP TABLE IF EXISTS users CASCADE;
DROP TABLE IF EXISTS vehicles CASCADE;
DROP TABLE IF EXISTS insurance_offers CASCADE;

CREATE TABLE users (
    id bigint PRIMARY KEY NOT NULL,
    nick text NOT NULL,
    login text UNIQUE NOT NULL, 
    password text NOT NULL,
    insert_time timestamp NOT NULL DEFAULT now()
);

CREATE TABLE vehicles (
    id bigint PRIMARY KEY NOT NULL,
    login text NOT NULL REFERENCES users(login),
    brand text NOT NULL,
    model text NOT NULL,
    insert_time timestamp NOT NULL
);
CREATE TABLE insurance_offers (
    id bigint PRIMARY KEY NOT NULL,
    vehicle_id bigint NOT NULL REFERENCES vehicles(id),
    insurer text NOT NULL,
    price float NOT NULL,
    insert_time timestamp NOT NULL
);

INSERT INTO users (id, nick, login, password, insert_time)
VALUES
    (1, 'Wojtek', 'wojtek', 'test', NOW()),
    (2, 'Asia', 'asia', 'test', NOW()),
    (3, 'Basia', 'basia', 'test', NOW());

INSERT INTO vehicles (id, login, brand, model, insert_time)
VALUES
    (1, 'wojtek', 'Toyota', 'Supra', NOW()),
    (2, 'wojtek', 'Honda', 'Accord', NOW()),
    (3, 'asia', 'Ford', 'Escort', NOW()),
    (4, 'basia', 'Nissan', 'GT-R', NOW());

INSERT INTO insurance_offers (id, vehicle_id, insurer, price, insert_time)
VALUES
    (1, 1, 'ABC Insurance', 500.00, NOW()),
    (2, 2, 'XYZ Insurance', 600.50, NOW()),
    (3, 3, '123 Insurance', 460.75, NOW()),
    (4, 1, '321 Insurance', 460.75, NOW()),
    (5, 2, '321 Insurance', 460.75, NOW()),
    (6, 4, '321 Insurance', 999999.75, NOW()),
    (7, 3, '321 Insurance', 460.75, NOW());

