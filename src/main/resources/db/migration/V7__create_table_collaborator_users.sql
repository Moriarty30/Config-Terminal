CREATE TABLE collaborator_users (
	id varchar(255) NOT NULL,
	email varchar(255) NOT NULL,
	name varchar(255) NOT NULL,
	phone_number varchar(255) NOT NULL,
	created_at timestamp NOT NULL,
	created_at_tz timestamp NOT NULL,
	collaborator_id varchar(255) NOT NULL,
	CONSTRAINT collaborators_users_pk PRIMARY KEY (id),
	CONSTRAINT collaborator_users_collaborator_fk FOREIGN KEY (collaborator_id) REFERENCES collaborators(id)
);

ALTER TABLE collaborators ADD city VARCHAR(255);
ALTER TABLE collaborators ADD state VARCHAR(255);
ALTER TABLE collaborators ADD payer_document_number VARCHAR(255);
ALTER TABLE collaborators ADD payer_document_type VARCHAR(255);
ALTER TABLE collaborators ADD payer_address TEXT;
ALTER TABLE collaborators ADD payer_email TEXT;
ALTER TABLE collaborators ADD payer_name TEXT;
ALTER TABLE collaborators ADD payer_phone_number VARCHAR(255);
ALTER TABLE collaborators ADD created_at_tz TIMESTAMP;