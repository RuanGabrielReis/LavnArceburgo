INSERT INTO usuario
(nome, cpf, telefone, rg, endereco, cidade, email)
VALUES
(
    'Administrador Lavn',
    '333.333.333-33',
    '35999999999',
    'MG333333',
    'Lavn Language Center',
    'Arceburgo',
    'master@lavn.com'
);

INSERT INTO funcionario
(codfuncionario, cargo, salario, senha)
VALUES
(
    LAST_INSERT_ID(),
    'MASTER',
    0.00,
    '$2a$10$XRDSYmvGH0tp3rVIs9xhYeN7bGgKgxyW/BzZXHh0C5u2PIuZI7V.C'
);