DROP TABLE IF EXISTS password_reset_token;
CREATE TABLE password_reset_token
(
    id 					BIGINT AUTO_INCREMENT,
    token 			    VARCHAR(512) NOT NULL UNIQUE ,
    expires_at          TIMESTAMP NOT NULL,
    user_id 		    BIGINT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW() ON UPDATE NOW(),
    PRIMARY KEY(id),
    INDEX (token, expires_at)
);