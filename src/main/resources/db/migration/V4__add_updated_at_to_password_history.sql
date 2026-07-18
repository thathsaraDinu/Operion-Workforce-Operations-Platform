-- Add updated_at column to password_history table
ALTER TABLE password_history 
ADD COLUMN updated_at DATETIME(6) NULL;
