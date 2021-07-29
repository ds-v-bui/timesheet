DROP TABLE IF EXISTS access_token;
CREATE TABLE access_token
(
    id 					BIGINT AUTO_INCREMENT,
    token 			    VARCHAR(512) NOT NULL UNIQUE ,
    expires_at          TIMESTAMP NOT NULL,
    locked			    BOOLEAN DEFAULT FALSE ,
    user_id 		    BIGINT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW() ON UPDATE NOW(),
    PRIMARY KEY(id),
    INDEX (token, expires_at)
);