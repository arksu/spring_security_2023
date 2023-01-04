insert into role (id, name)
values (1, 'ADMIN'),
       (2, 'MODERATOR');

insert into users(id, login, password_hash)
values ('74e38748-8ad6-11ed-a1eb-0242ac120002', 'admin',
        '$2a$12$xUUFRlHngidkC/RgWUXlCOEcpzyTNB4Jd3MAGELdrrmaKx0cMiQsS');

insert into users_roles (user_id, roles_id)
values ('74e38748-8ad6-11ed-a1eb-0242ac120002', 1);