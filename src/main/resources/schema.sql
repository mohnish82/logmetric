/*
create database logmetric;
use logmetric;

create table logs(id varchar(50), access_time date, source_ip varchar(50));

create user 'logan'@'localhost' identified by 'logan';
grant all on logmetric.* to 'logan'@'localhost';
*/