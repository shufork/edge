--liquibase formatted SQL
--changeset cj:1

-- see JdbcTokenStore
DROP TABLE IF EXISTS `oauth_access_token`;
CREATE TABLE `oauth_access_token` (
    `token_id` VARCHAR(256) NOT NULL,
    `token` BLOB NOT NULL,
    `authentication_id` VARCHAR(256) NOT NULL,
    `user_name` VARCHAR(256) NULL,
    `client_id` VARCHAR(256) NOT NULL,
    `authentication` BLOB NOT NULL,
    `refresh_token` VARCHAR(256) NOT NULL,
    PRIMARY KEY (`authentication_id`) USING BTREE
)  ENGINE=INNODB DEFAULT CHARSET=UTF8;
CREATE INDEX idx_qyr_token_id ON oauth_access_token ( token_id );
CREATE INDEX idx_qyr_authentication_id ON oauth_access_token ( authentication_id );
CREATE INDEX idx_qyr_user_name_client_id ON oauth_access_token ( user_name, client_id );
CREATE INDEX idx_qyr_refresh_token_id ON oauth_access_token ( refresh_token );

-- see JdbcClientTokenServices
DROP TABLE IF EXISTS `oauth_client_token`;
CREATE TABLE oauth_client_token (
    `authentication_id` VARCHAR ( 256 ) NOT NULL,
    `token_id` VARCHAR ( 256 ) NOT NULL,
    `token` BLOB NOT NULL,
    `user_name` VARCHAR ( 256 )  NULL,
    `client_id` VARCHAR ( 256 ) NOT NULL,
PRIMARY KEY ( `authentication_id` )
) ENGINE = INNODB DEFAULT CHARSET = utf8;

-- see JdbcTokenStore
DROP TABLE IF EXISTS `oauth_refresh_token`;
CREATE TABLE `oauth_refresh_token` (
    `token_id` VARCHAR(256) NOT NULL,
    `token` BLOB NOT NULL,
    `authentication` BLOB NOT NULL,
    PRIMARY KEY (`token_id`)
) ENGINE=INNODB DEFAULT CHARSET=UTF8;

-- see JdbcApprovalStore
DROP TABLE IF EXISTS `oauth_approvals`;
CREATE TABLE `oauth_approvals` (
    `userid` VARCHAR(256) NOT NULL,
    `clientid` VARCHAR(256) NOT NULL,
    `scope` VARCHAR(256) NOT NULL,
    `status` VARCHAR(10) NOT NULL,
    `expiresat` TIMESTAMP NOT NULL,
    `lastmodifiedat` TIMESTAMP NOT NULL
)  ENGINE=INNODB DEFAULT CHARSET=UTF8;
CREATE INDEX idx_qyr_userId_clientId_scope ON oauth_approvals ( userid, clientid, scope );

-- see JdbcAuthorizationCodeServices
DROP TABLE IF EXISTS `oauth_code`;
CREATE TABLE `oauth_code` (
    `code` VARCHAR(256) NOT NULL,
    `authentication` BLOB NOT NULL,
    PRIMARY KEY (`code`)
)  ENGINE=INNODB DEFAULT CHARSET=UTF8;

-- see JdbcClientDetailsService
DROP TABLE IF EXISTS `oauth_client_details`;
CREATE TABLE `oauth_client_details` (
    `client_id` VARCHAR(255) NOT NULL,
    `resource_ids` VARCHAR(256) DEFAULT NULL,
    `client_secret` VARCHAR(256) DEFAULT NULL,
    `scope` VARCHAR(256) DEFAULT NULL,
    `authorized_grant_types` VARCHAR(256) DEFAULT NULL,
    `web_server_redirect_uri` VARCHAR(256) DEFAULT NULL,
    `authorities` VARCHAR(256) DEFAULT NULL,
    `access_token_validity` INT(11) DEFAULT NULL,
    `refresh_token_validity` INT(11) DEFAULT NULL,
    `additional_information` VARCHAR(4096) DEFAULT NULL,
    `autoapprove` VARCHAR(45) DEFAULT 'true',
    PRIMARY KEY (`client_id`)
)  ENGINE=INNODB DEFAULT CHARSET=UTF8;


--changeset cj:2

INSERT INTO oauth_client_details (client_id, resource_ids, client_secret, scope, authorized_grant_types, web_server_redirect_uri, authorities, access_token_validity, refresh_token_validity, additional_information, autoapprove)
VALUES
('ui', NULL, 'secret', 'ui-scope', 'authorization_code,password,refresh_token,client_credentials', '','role_client, role_trusted_client', '30000', '30000', NULL, 'false');

INSERT INTO oauth_client_details (client_id, resource_ids, client_secret, scope, authorized_grant_types, web_server_redirect_uri, authorities, access_token_validity, refresh_token_validity, additional_information, autoapprove)
VALUES
  ('api', NULL, 'secret', 'server-scope', 'client_credentials', '', 'role_client, role_trusted_client', '30000', '30000', NULL, 'false');