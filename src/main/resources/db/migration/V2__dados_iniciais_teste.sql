INSERT INTO usuario
(nome, cpf, telefone, rg, endereco, cidade, email)
VALUES
(
    'Professor Teste',
    '111.111.111-11',
    '35999999999',
    'MG111111',
    'Rua Teste, 100',
    'Arceburgo',
    'professor@lavn.com'
);

INSERT INTO funcionario
(codfuncionario, cargo, salario)
VALUES
(
    1,
    'PROFESSOR',
    3000.00
);

INSERT INTO classe
(nivel, codprofessor)
VALUES
(
    'INTERMEDIARIO',
    1
);

INSERT INTO usuario
(nome, cpf, telefone, rg, endereco, cidade, email)
VALUES
(
    'Aluno Teste',
    '222.222.222-22',
    '35988888888',
    'MG222222',
    'Rua Aluno, 200',
    'Arceburgo',
    'aluno@teste.com'
);

INSERT INTO aluno
(codaluno, codclasse)
VALUES
(
    2,
    1
);

INSERT INTO aula
(codclasse, diahora)
VALUES
(
    1,
    '2026-08-20 19:00:00'
);