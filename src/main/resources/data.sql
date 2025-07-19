INSERT INTO users (email, password, first_name, last_name) 
VALUES ('admin@taskmanager.com', '$2a$10$K8v7Q8QZW8QZWQJq8v7Q8.K8v7Q8QZW8QZWQJq8v7Q8K8v7Q8QZW8', 'Admin', 'User')
ON CONFLICT (email) DO NOTHING;
