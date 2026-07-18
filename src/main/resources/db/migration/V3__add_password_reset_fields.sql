-- Add password reset fields to employee table
ALTER TABLE employee 
ADD COLUMN password_reset_token VARCHAR(255) NULL,
ADD COLUMN password_reset_token_expiry DATETIME(6) NULL,
ADD COLUMN password_version INT DEFAULT 1,
ADD COLUMN failed_login_attempts INT DEFAULT 0,
ADD COLUMN account_locked_until DATETIME(6) NULL;

-- Create password history table
CREATE TABLE password_history (
  id BIGINT NOT NULL AUTO_INCREMENT,
  employee_id BIGINT NOT NULL,
  password_hash VARCHAR(255) NOT NULL,
  created_at DATETIME(6) NOT NULL,
  PRIMARY KEY (id),
  KEY FK_password_history_employee (employee_id),
  CONSTRAINT FK_password_history_employee FOREIGN KEY (employee_id) REFERENCES employee(id)
) ENGINE=InnoDB;
