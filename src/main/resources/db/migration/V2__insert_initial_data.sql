INSERT INTO department (name)
VALUES
    ('Engineering'),
    ('Human Resources'),
    ('Finance');


INSERT INTO employee
(first_name, last_name, email, password, department_id)
VALUES
    (
        'Admin',
        'User',
        'admin@operion.com',
        '$2a$12$pdqkfr9ddzaMd3yVe3DrmucaGlNGaKgKOmF0R43GDJZw0xTjpHGS2',
        1
    );