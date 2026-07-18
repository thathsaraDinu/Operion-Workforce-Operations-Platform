INSERT INTO department (name)
VALUES
    ('Engineering'),
    ('Human Resources'),
    ('Finance');


INSERT INTO employee
(first_name, last_name, email, password, role, department_id)
VALUES
    (
        'Admin',
        'User',
        'admin@operion.com',
        '$2a$12$HmyrN3Ly1BV9KrQnJ.Y5yepy6HfooH.2ea8d9oYbhbn0MVo.4aT76',
        'ADMIN',
        1
    );