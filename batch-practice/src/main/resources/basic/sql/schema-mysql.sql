CREATE  TABLE IF NOT EXISTS account_summary (
  id INT NOT NULL AUTO_INCREMENT ,
  account_number VARCHAR(10) NOT NULL ,
  current_balance DECIMAL(10,2) NOT NULL ,
  PRIMARY KEY (id) )
ENGINE = InnoDB;

CREATE  TABLE IF NOT EXISTS transaction (
  id INT NOT NULL AUTO_INCREMENT ,
  timestamp TIMESTAMP NOT NULL ,
  amount DECIMAL(8,2) NOT NULL ,
  account_summary_id INT NOT NULL ,
  PRIMARY KEY (id) ,
  INDEX fk_Transaction_Account_Summary (account_summary_id ASC) ,
  CONSTRAINT fk_Transaction_Account_Summary
    FOREIGN KEY (account_summary_id )
    REFERENCES Account_Summary (id )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;

CREATE TABLE CUSTOMER  (
                           id BIGINT  NOT NULL PRIMARY KEY ,
                           first_name VARCHAR(11) NOT NULL ,
                           middle_initial VARCHAR(1),
                           last_name VARCHAR(15) NOT NULL,
                           address VARCHAR(45) NOT NULL,
                           city VARCHAR(16) NOT NULL,
                           state CHAR(2) NOT NULL,
                           zip_code CHAR(5)
);
