CREATE SCHEMA management AUTHORIZATION postgres;

CREATE TABLE management.authentication (
	id serial NOT NULL,
	user_name varchar(30) NOT NULL,
	channel_id varchar(30) NOT NULL,
	application_id varchar(30) NOT NULL,
	authentication_time timestamp NOT NULL,
	authentication_ip varchar(30) NOT NULL,
	authentication_device varchar(30) NOT NULL,
	access_token varchar(30) NOT NULL,
	refresh_token varchar(30) NOT NULL,
	CONSTRAINT authentication_pkey PRIMARY KEY (id),
	CONSTRAINT authentication_un UNIQUE (user_name)
);