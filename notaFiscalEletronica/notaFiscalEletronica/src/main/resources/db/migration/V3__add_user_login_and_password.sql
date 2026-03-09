CREATE TABLE usuarios (
    id UUID PRIMARY KEY,
    login VARCHAR(255) NOT NULL UNIQUE,
    senha VARCHAR(255) NOT NULL
);


INSERT INTO usuarios (id, login, senha) 
VALUES (gen_random_uuid(), 'admin@nfe.com', '$2a$10$Y50UaM2h24WEl8vAY9Z04un7z7989ECS799yNm26Wv8Y6e9m06Lya');