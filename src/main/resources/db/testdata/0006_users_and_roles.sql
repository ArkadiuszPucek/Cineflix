insert into
    users (email, password, avatar)
values
    ('admin@example.com', '{noop}Adminpass12!', '/images/avatars/wilk2.png'),
    ('user@example.com', '{noop}Userpass12!', '/images/avatars/Fox.png'),
    ('editor@example.com', '{noop}Editorpass12!', '/images/avatars/wilk2.png');

insert into
    user_role (name, description)
values
    ('ADMIN', 'pełne uprawnienia'),
    ('USER', 'podstawowe uprawnienia, możliwość oddawania głosów'),
    ('EDITOR', 'podstawowe uprawnienia + możliwość zarządzania treściami');

insert into
    user_roles (user_id, role_id)
values
    (1, 1),
    (2, 2),
    (3, 3);