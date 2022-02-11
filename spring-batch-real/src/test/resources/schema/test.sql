CREATE TABLE IF NOT EXISTS ncustomer  (
                            customer_id integer  NOT NULL  PRIMARY KEY  ,
                            first_name VARCHAR(45) NOT NULL ,
                            middle_name VARCHAR(45),
                            last_name VARCHAR(45) NOT NULL,
                            address1 VARCHAR(255) NOT NULL,
                            address2 VARCHAR(255),
                            city VARCHAR(50) NOT NULL,
                            state VARCHAR(20) NOT NULL,
                            postal_code CHAR(5) NOT NULL,
                            ssn VARCHAR(11) NOT NULL,
                            email_address VARCHAR(255),
                            home_phone VARCHAR(12),
                            cell_phone VARCHAR(12),
                            work_phone VARCHAR(12),
                            notification_pref CHAR(1) NOT NULL
) ;

CREATE TABLE IF NOT EXISTS  naccount  (
                           account_id BIGINT  NOT NULL AUTO_INCREMENT PRIMARY KEY  ,
                           balance FLOAT NOT NULL ,
                           last_statement_date TIMESTAMP NOT NULL
) ;
