-- Recria o banco (pra reset diário)
DROP DATABASE IF EXISTS lavn;
CREATE DATABASE lavn
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE lavn;

-- =========================
-- TABELA: classe
-- =========================
CREATE TABLE classe (
                        codclasse INT AUTO_INCREMENT PRIMARY KEY,
                        nivel VARCHAR(50) NOT NULL,
                        numeroalunos INT NOT NULL DEFAULT 0,
                        professor VARCHAR(100) NULL
) ENGINE=InnoDB;

-- =========================
-- TABELA: usuario (superclasse)
-- CPF com pontuação: 000.000.000-00 => 14 chars
-- =========================
CREATE TABLE usuario (
                         codusuario INT AUTO_INCREMENT PRIMARY KEY,
                         nome VARCHAR(120) NOT NULL,
                         cpf CHAR(14) NOT NULL UNIQUE,
                         telefone VARCHAR(20) NULL,
                         rg VARCHAR(20) NULL,
                         endereco VARCHAR(150) NULL,
                         cidade VARCHAR(80) NULL,
                         email VARCHAR(150) NOT NULL UNIQUE
) ENGINE=InnoDB;

-- =========================
-- TABELA: funcionario (subclasse de usuario)
-- PK = FK para usuario
-- =========================
CREATE TABLE funcionario (
                             codfuncionario INT PRIMARY KEY,
                             cargo VARCHAR(80) NOT NULL,
                             salario DECIMAL(10,2) NOT NULL DEFAULT 0.00,
                             CONSTRAINT fk_funcionario_usuario
                                 FOREIGN KEY (codfuncionario) REFERENCES usuario(codusuario)
                                     ON DELETE CASCADE
                                     ON UPDATE CASCADE
) ENGINE=InnoDB;

-- =========================
-- TABELA: aluno (subclasse de usuario)
-- codclasse dentro de aluno (como você pediu)
-- =========================
CREATE TABLE aluno (
                       codaluno INT PRIMARY KEY,
                       nivel VARCHAR(50) NOT NULL,
                       codclasse INT NULL,
                       CONSTRAINT fk_aluno_usuario
                           FOREIGN KEY (codaluno) REFERENCES usuario(codusuario)
                               ON DELETE CASCADE
                               ON UPDATE CASCADE,
                       CONSTRAINT fk_aluno_classe
                           FOREIGN KEY (codclasse) REFERENCES classe(codclasse)
                               ON DELETE SET NULL
                               ON UPDATE CASCADE
) ENGINE=InnoDB;

-- =========================
-- TABELA: horario
-- =========================
CREATE TABLE horario (
                         codhorario INT AUTO_INCREMENT PRIMARY KEY,
                         duracaoaula INT NOT NULL,       -- minutos
                         codclasse INT NOT NULL,
                         sala VARCHAR(30) NULL,
                         diahora DATETIME NOT NULL,
                         CONSTRAINT fk_horario_classe
                             FOREIGN KEY (codclasse) REFERENCES classe(codclasse)
                                 ON DELETE CASCADE
                                 ON UPDATE CASCADE
) ENGINE=InnoDB;

-- =========================
-- TABELA: aula
-- (presenca NÃO fica aqui, porque é por aluno)
-- =========================
CREATE TABLE aula (
                      codaula INT AUTO_INCREMENT PRIMARY KEY,
                      codclasse INT NOT NULL,
                      diahora DATETIME NOT NULL,
                      CONSTRAINT fk_aula_classe
                          FOREIGN KEY (codclasse) REFERENCES classe(codclasse)
                              ON DELETE CASCADE
                              ON UPDATE CASCADE
) ENGINE=InnoDB;

-- =========================
-- TABELA: presenca (por aluno por aula)
-- Chave composta (codaluno, codaula) pra evitar duplicar presença do mesmo aluno na mesma aula
-- =========================
CREATE TABLE presenca (
                          codaluno INT NOT NULL,
                          codaula INT NOT NULL,
                          presente TINYINT(1) NOT NULL DEFAULT 0,
                          observacao VARCHAR(255) NULL,
                          PRIMARY KEY (codaluno, codaula),
                          CONSTRAINT fk_presenca_aluno
                              FOREIGN KEY (codaluno) REFERENCES aluno(codaluno)
                                  ON DELETE CASCADE
                                  ON UPDATE CASCADE,
                          CONSTRAINT fk_presenca_aula
                              FOREIGN KEY (codaula) REFERENCES aula(codaula)
                                  ON DELETE CASCADE
                                  ON UPDATE CASCADE
) ENGINE=InnoDB;

-- =========================
-- TABELA: anotacoes
-- (sobre aluno e acontece em aula)
-- =========================
CREATE TABLE anotacoes (
                           codanotacao INT AUTO_INCREMENT PRIMARY KEY,
                           tipo VARCHAR(50) NOT NULL,
                           texto TEXT NOT NULL,
                           codaluno INT NOT NULL,
                           codaula INT NOT NULL,
                           CONSTRAINT fk_anotacoes_aluno
                               FOREIGN KEY (codaluno) REFERENCES aluno(codaluno)
                                   ON DELETE CASCADE
                                   ON UPDATE CASCADE,
                           CONSTRAINT fk_anotacoes_aula
                               FOREIGN KEY (codaula) REFERENCES aula(codaula)
                                   ON DELETE CASCADE
                                   ON UPDATE CASCADE
) ENGINE=InnoDB;

-- Índices úteis
CREATE INDEX idx_aluno_codclasse ON aluno(codclasse);
CREATE INDEX idx_aula_codclasse_data ON aula(codclasse, diahora);
CREATE INDEX idx_horario_codclasse_data ON horario(codclasse, diahora);
CREATE INDEX idx_anotacoes_aluno_aula ON anotacoes(codaluno, codaula);