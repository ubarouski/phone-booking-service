DROP TABLE IF EXISTS device_info;
create TABLE IF NOT EXISTS device_info
(
    id          SERIAL PRIMARY KEY,
    brand       VARCHAR(256),
    model       VARCHAR(256) NOT NULL,
    band        VARCHAR(256),
    device_os   VARCHAR(256)
);
DROP TABLE IF EXISTS phone;
create TABLE IF NOT EXISTS phone
(
    imei           numeric (15) PRIMARY KEY,
    device_id      VARCHAR(256) NOT NULL,
    user_name      VARCHAR(256),
    booking_date   timestamp
);

insert into device_info(brand, model, band, device_os) values ('Samsung', 'Galaxy S9', '4g', 'Android');
insert into device_info(brand, model, band, device_os) values ('Samsung', 'Galaxy S8', '4g', 'Android');
insert into device_info(brand, model, band, device_os) values ('Motorola', 'Nexus 6', '4g', 'Android');
insert into device_info(brand, model, band, device_os) values ('Oneplus', '9', '4g', 'Android');
insert into device_info(brand, model, band, device_os) values ('Apple', 'iPhone 13', '4g', 'iOS');
insert into device_info(brand, model, band, device_os) values ('Apple', 'iPhone 12', '4g', 'iOS');
insert into device_info(brand, model, band, device_os) values ('Apple', 'iPhone 11', '4g', 'iOS');
insert into device_info(brand, model, band, device_os) values ('Apple', 'iPhone X', '4g', 'iOS');
insert into device_info(brand, model, band, device_os) values ('Nokia', '3310', '2g', 'Series 20');

insert into phone(imei, device_id) values (355623112522301, 1);
insert into phone(imei, device_id) values (355623112522302, 2);
insert into phone(imei, device_id) values (355623112522303, 2);
insert into phone(imei, device_id) values (355623112522304, 3);
insert into phone(imei, device_id) values (355623112522305, 4);
insert into phone(imei, device_id) values (355623112522306, 5);
insert into phone(imei, device_id) values (355623112522307, 6);
insert into phone(imei, device_id) values (355623112522308, 7);
insert into phone(imei, device_id) values (355623112522309, 8);
insert into phone(imei, device_id) values (355623112522310, 9);
