CREATE TABLE app_user (
    id BINARY(16) NOT NULL UNIQUE,
    password_hash VARCHAR(64),
    first_name VARCHAR(64),
    last_name VARCHAR(64),
    email VARCHAR(30) UNIQUE,
    phone_number VARCHAR(15) UNIQUE,
    image_url VARCHAR(256),
    activated BIT NOT NULL,
    user_type VARCHAR(11) NOT NULL,
    created_by VARCHAR(255),
    creation_date DATETIME,
    last_modified_by VARCHAR(255),
    last_modified_date DATETIME,

    PRIMARY KEY(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;


CREATE TABLE authority (
    id BINARY(16) NOT NULL UNIQUE,
    authority_name VARCHAR(64) NOT NULL ,
    created_by VARCHAR(255),
    creation_date DATETIME,
    last_modified_by VARCHAR(255),
    last_modified_date DATETIME,

    PRIMARY KEY(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

CREATE TABLE user_authority (
    user_id BINARY(16) NOT NULL,
    authority_id BINARY(16) NOT NULL,

    PRIMARY KEY(user_id, authority_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;