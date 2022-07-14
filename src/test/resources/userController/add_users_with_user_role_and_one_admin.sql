insert into users (id, email, first_name, last_name, password_hash, role)
values (1, 'test1@test.com', 'test1', 'test1LastName', '$2a$10$5k99tvpc.Vi6fua8d9GOyOA2iyIDqgR.HQa1hHn1pZ9ajvdWlt3Um', 0),
       (2, 'test2@test.com', 'test2', 'test2LastName', '$2a$10$5k99tvpc.Vi6fua8d9GOyOA2iyIDqgR.HQa1hHn1pZ9ajvdWlt3Um', 1),
       (3, 'test3@test.com', 'test3', 'test3LastName', '$2a$10$5k99tvpc.Vi6fua8d9GOyOA2iyIDqgR.HQa1hHn1pZ9ajvdWlt3Um', 1);