DROP TABLE IF EXISTS customer;
DROP TABLE IF EXISTS portfolio_item;

CREATE TABLE customer
(
    id      int AUTO_INCREMENT PRIMARY KEY,
    name    varchar(50),
    balance int
);

CREATE TABLE portfolio_item
(
    id          int AUTO_INCREMENT PRIMARY KEY,
    customer_id int,
    ticker      VARCHAR(10),
    quantity    int,
    foreign key (customer_id) references customer (id)
);
