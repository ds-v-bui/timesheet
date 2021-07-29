DROP TABLE IF EXISTS userDTOS;
CREATE TABLE userDTOS
(
    id 					BIGINT AUTO_INCREMENT,
    first_name 			VARCHAR(100) NOT NULL,
    last_name			VARCHAR(100) NOT NULL,
    phone_number 		VARCHAR(12),
    avatar				TEXT,
    gender				SMALLINT,
    email				VARCHAR(100) NOT NULL,
    password			VARCHAR(256) NOT NULL,
    birth_day			DATE,
    role				VARCHAR(50) NOT NULL,
    address				VARCHAR(100),
    deleted_at		TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW() ON UPDATE NOW(),
    PRIMARY KEY(id)
);

INSERT INTO userDTOS(id, email, first_name, last_name, password, role)
VALUES
(1, 'cuong-nguyen@dimage.co.jp', 'Cuong', 'Nguyen', '$2a$10$QRU8kA/o/qyOQSHmQUAi4.wIzHmmbwTnd8zkuualYJgnP/uNydjS.', 'userDTO'),
(2, 'admin@dimage.co.jp', 'Admin', 'Admin', '$2a$10$QRU8kA/o/qyOQSHmQUAi4.wIzHmmbwTnd8zkuualYJgnP/uNydjS.', 'admin');
