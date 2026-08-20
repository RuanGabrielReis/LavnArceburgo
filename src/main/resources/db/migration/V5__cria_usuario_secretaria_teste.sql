INSERT INTO usuario
(nome, cpf, telefone, rg, endereco, cidade, email)
VALUES
    (
        'Secretaria Teste',
        '444.444.444-44',
        '35966666666',
        'MG444444',
        'Rua Secretaria, 400',
        'Arceburgo',
        'secretaria@lavn.com'
    );

INSERT INTO funcionario
(codfuncionario, cargo, salario, senha)
VALUES
    (
        LAST_INSERT_ID(),
        'SECRETARIA',
        2500.00,
        '$2a$10$iWDN9MaOnZYtJnziQbdW8OATC3zRbl5agS7W6HjhI1p/3MKIt/inu'
    );