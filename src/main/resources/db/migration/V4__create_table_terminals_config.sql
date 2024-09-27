CREATE TABLE terminals_configs (
	id varchar(255) NOT NULL,
	terminal_id varchar(255) NOT NULL,
	code varchar(255) NOT NULL,
	type varchar(255) NOT NULL,
	value varchar(255) NOT NULL,
	created_at timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
	created_at_tz timestamp NOT NULL,
	CONSTRAINT terminals_configs_pkey PRIMARY KEY (id)
);