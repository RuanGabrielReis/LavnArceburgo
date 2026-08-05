USE lavn;

-- CLASSES
INSERT INTO classe (nivel, numeroalunos, professor)
VALUES
('1A', 0, 'Prof. Maria'),
('2B', 0, 'Prof. João');

-- USUÁRIOS (alguns serão alaunos, outros funcionários)
INSERT INTO usuario (nome, cpf, telefone, rg, endereco, cidade, email)
VALUES
('Ana Souza',   '123.456.789-01', '11 99999-0001', 'MG-123', 'Rua A, 10', 'Cidade X', 'ana@escola.local'),
('Bruno Lima',  '123.456.789-02', '11 99999-0002', 'MG-124', 'Rua B, 20', 'Cidade X', 'bruno@escola.local'),
('Carla Admin', '123.456.789-03', '11 99999-0003', 'MG-125', 'Rua C, 30', 'Cidade Y', 'carla@escola.local');

-- FUNCIONÁRIO (Carla)
INSERT INTO funcionario (codfuncionario, cargo, salario)
VALUES
(3, 'Coordenadora', 3500.00);

-- ALUNOS (Ana e Bruno) ligados a classes
INSERT INTO aluno (codaluno, nivel, codclasse)
VALUES
(1, 'iniciante', 1),
(2, 'intermediario', 2);

-- Atualiza numeroalunos (opcional)
UPDATE classe
SET numeroalunos = (
  SELECT COUNT(*) FROM aluno a WHERE a.codclasse = classe.codclasse
);

-- HORÁRIOS
INSERT INTO horario (duracaoaula, codclasse, sala, diahora)
VALUES
(50, 1, 'Sala 01', '2026-03-19 08:00:00'),
(50, 2, 'Sala 02', '2026-03-19 09:00:00');

-- AULAS
INSERT INTO aula (codclasse, diahora)
VALUES
(1, '2026-03-19 08:00:00'),
(2, '2026-03-19 09:00:00');

-- PRESENÇAS (por aluno por aula)
-- Ana presente na aula 1
INSERT INTO presenca (codaluno, codaula, presente, observacao)
VALUES
(1, 1, 1, NULL),
-- Bruno faltou na aula 2
(2, 2, 0, 'Faltou');

-- ANOTAÇÕES (sobre aluno, na aula)
INSERT INTO anotacoes (tipo, texto, codaluno, codaula)
VALUES
('comportamento', 'Participou bem da aula.', 1, 1),
('atividade', 'Faltou entregar a tarefa.', 2, 2);