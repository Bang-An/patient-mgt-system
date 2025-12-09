-- Ensure the 'users' table exists
CREATE TABLE IF NOT EXISTS "users" (
                                       id UUID PRIMARY KEY,
                                       name VARCHAR(255) NOT NULL,
                                       date_of_birth DATE NOT NULL,
                                       email VARCHAR(255) UNIQUE NOT NULL,
                                       password VARCHAR(255) NOT NULL,
                                       role VARCHAR(50) NOT NULL
);

-- Insert the user if no existing user with the same id or email exists
INSERT INTO "users" (id, name, date_of_birth, email, password, role)
SELECT '223e4567-e89b-12d3-a456-426614174006',
       'test user',
       '1995-06-15',
       'testuser@test.com',
       '$2b$12$7hoRZfJrRKD2nIm2vHLs7OBETy.LWenXXMLKf99W8M4PUwO6KB7fu',
       'ADMIN'
WHERE NOT EXISTS (
    SELECT 1 FROM "users"
    WHERE id = '223e4567-e89b-12d3-a456-426614174006'
       OR email = 'testuser@test.com'
);

