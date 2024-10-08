CREATE TABLE collaborators (
	id varchar(255) NOT NULL,
	document_number varchar(255) NOT NULL,
	document_type varchar(255) NOT NULL,
	address varchar(255) NULL,
	email varchar(255) NOT NULL,
	name varchar(255) NOT NULL,
	phone_number varchar(255) NOT NULL,
	created_at timestamp NOT NULL,
	CONSTRAINT collaborators_pk PRIMARY KEY (id)
);

CREATE TABLE commerces (
	id varchar(255) NOT NULL,
	name varchar(255) NOT NULL,
	nit varchar(255) NOT NULL,
	enabled bool NOT NULL,
	created_at timestamp NOT NULL,
	CONSTRAINT commerces_pkey PRIMARY KEY (id)
);

CREATE TABLE config (
	id varchar(255) NULL,
	collaborator_portal_external_id_seq numeric NULL,
	CONSTRAINT config_pkey PRIMARY KEY (id)
);

CREATE TABLE payment_methods (
	id varchar(255) NOT NULL,
	name varchar(255) NOT NULL,
	code varchar(255) NOT NULL,
	enabled bool NOT NULL,
	created_at timestamp NULL,
	CONSTRAINT payment_methods_pkey PRIMARY KEY (id)
);

CREATE TABLE terminals (
	id varchar(255) NOT NULL,
	code varchar(255) NULL,
	commerce_id varchar(255) NULL,
	created_at timestamp NULL,
	enabled bool NULL,
	CONSTRAINT terminals_pk PRIMARY KEY (id)
);

ALTER TABLE terminals ADD CONSTRAINT terminals_commerce_fk FOREIGN KEY (commerce_id) REFERENCES commerces(id);

CREATE TABLE terminals_payment_methods (
	id varchar(255) NOT NULL,
	payment_method_id varchar(255) NULL,
	terminal_id varchar(255) NULL,
	CONSTRAINT terminals_payment_methods_pk PRIMARY KEY (id)
);

ALTER TABLE terminals_payment_methods ADD CONSTRAINT terminals_payment_methods_fk FOREIGN KEY (terminal_id) REFERENCES terminals(id);
ALTER TABLE terminals_payment_methods ADD CONSTRAINT terminals_payment_methods_fk_1 FOREIGN KEY (payment_method_id) REFERENCES payment_methods(id);

CREATE TABLE transactions_plain (
	id varchar NOT NULL,
	billing_document varchar NULL,
	billing_document_type varchar NULL,
	branch_name varchar NULL,
	cancel_callback_url varchar NULL,
	client varchar NULL,
	country_iso_code varchar NULL,
	currency varchar NULL,
	customer_address varchar NULL,
	customer_email varchar NULL,
	customer_name varchar NULL,
	customer_phone varchar NULL,
	enable_niubiz_setting varchar NULL,
	error_callback_url varchar NULL,
	external_id varchar NULL,
	extra_tax_amount varchar NULL,
	invoice_number varchar NULL,
	max_installment_count varchar NULL,
	method varchar NULL,
	mode varchar NULL,
	origin varchar NULL,
	payment_requires_login varchar NULL,
	payment_shipping_address varchar NULL,
	redirect_callback_url varchar NULL,
	success_callback_url varchar NULL,
	tax_amount varchar NULL,
	terminal_id varchar NULL,
	total_amount varchar NULL,
	selected_payment_method varchar NULL,
	created_at timestamp NULL,
	collaborator_id varchar NULL,
	CONSTRAINT trx_plain_pkey PRIMARY KEY (id)
);