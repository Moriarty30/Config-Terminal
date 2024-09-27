CREATE TABLE terminal_access (
	id varchar(255) NOT NULL,
	terminal_id varchar(255) NOT NULL,
	access_token varchar(255) NULL,
	expiration_date timestamp NOT NULL,
	created_at timestamp NOT NULL,
	CONSTRAINT terminal_access_pkey PRIMARY KEY (id)
);