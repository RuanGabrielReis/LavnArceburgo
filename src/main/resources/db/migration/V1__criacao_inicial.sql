-- ==========================================
-- TABELA: usuario
-- Superclasse de aluno e funcionario
-- ==========================================

CREATE TABLE usuario (
                         codusuario BIGINT AUTO_INCREMENT PRIMARY KEY,
                         nome VARCHAR(120) NOT NULL,
                         cpf CHAR(14) NOT NULL UNIQUE,
                         telefone VARCHAR(20),
                         rg VARCHAR(20),
                         endereco VARCHAR(150),
                         cidade VARCHAR(80),
                         email VARCHAR(150) NOT NULL UNIQUE
) ENGINE=InnoDB;


-- ==========================================
-- TABELA: funcionario
-- Subclasse de usuario
-- ==========================================

CREATE TABLE funcionario (
                             codfuncionario BIGINT PRIMARY KEY,
                             cargo VARCHAR(80) NOT NULL,
                             salario DECIMAL(10,2) NOT NULL DEFAULT 0.00,

                             CONSTRAINT fk_funcionario_usuario
                                 FOREIGN KEY (codfuncionario)
                                     REFERENCES usuario(codusuario)
                                     ON DELETE CASCADE
                                     ON UPDATE CASCADE
) ENGINE=InnoDB;


-- ==========================================
-- TABELA: classe
-- Cada classe possui um professor responsável
-- ==========================================

CREATE TABLE classe (
                        codclasse BIGINT AUTO_INCREMENT PRIMARY KEY,
                        nivel VARCHAR(50) NOT NULL,
                        codprofessor BIGINT NULL,

                        CONSTRAINT fk_classe_professor
                            FOREIGN KEY (codprofessor)
                                REFERENCES funcionario(codfuncionario)
                                ON DELETE SET NULL
                                ON UPDATE CASCADE
) ENGINE=InnoDB;


-- ==========================================
-- TABELA: aluno
-- Subclasse de usuario
-- Cada aluno pertence a no máximo uma classe
-- ==========================================

CREATE TABLE aluno (
                       codaluno BIGINT PRIMARY KEY,
                       codclasse BIGINT NULL,

                       CONSTRAINT fk_aluno_usuario
                           FOREIGN KEY (codaluno)
                               REFERENCES usuario(codusuario)
                               ON DELETE CASCADE
                               ON UPDATE CASCADE,

                       CONSTRAINT fk_aluno_classe
                           FOREIGN KEY (codclasse)
                               REFERENCES classe(codclasse)
                               ON DELETE SET NULL
                               ON UPDATE CASCADE,

                       CONSTRAINT uq_aluno_classe
                           UNIQUE (codaluno, codclasse)
) ENGINE=InnoDB;


-- ==========================================
-- TABELA: horario
-- Horário semanal planejado de uma classe
-- ==========================================

CREATE TABLE horario (
                         codhorario BIGINT AUTO_INCREMENT PRIMARY KEY,
                         duracaoaula INT NOT NULL,
                         codclasse BIGINT NOT NULL,
                         sala VARCHAR(30),
                         diasemana VARCHAR(20) NOT NULL,
                         horainicio TIME NOT NULL,

                         CONSTRAINT fk_horario_classe
                             FOREIGN KEY (codclasse)
                                 REFERENCES classe(codclasse)
                                 ON DELETE CASCADE
                                 ON UPDATE CASCADE
) ENGINE=InnoDB;


-- ==========================================
-- TABELA: aula
-- Ocorrência real de uma aula
-- ==========================================

CREATE TABLE aula (
                      codaula BIGINT AUTO_INCREMENT PRIMARY KEY,
                      codclasse BIGINT NOT NULL,
                      diahora DATETIME NOT NULL,

                      CONSTRAINT uq_aula_classe
                          UNIQUE (codaula, codclasse),

                      CONSTRAINT fk_aula_classe
                          FOREIGN KEY (codclasse)
                              REFERENCES classe(codclasse)
                              ON DELETE CASCADE
                              ON UPDATE CASCADE
) ENGINE=InnoDB;


-- ==========================================
-- TABELA: presenca
-- Presença de um aluno em uma aula
-- ==========================================

CREATE TABLE presenca (
                          codaluno BIGINT NOT NULL,
                          codaula BIGINT NOT NULL,
                          codclasse BIGINT NOT NULL,
                          presente BOOLEAN NOT NULL DEFAULT FALSE,
                          observacao VARCHAR(255),

                          PRIMARY KEY (codaluno, codaula),

                          CONSTRAINT fk_presenca_aluno_classe
                              FOREIGN KEY (codaluno, codclasse)
                                  REFERENCES aluno(codaluno, codclasse)
                                  ON DELETE CASCADE
                                  ON UPDATE CASCADE,

                          CONSTRAINT fk_presenca_aula_classe
                              FOREIGN KEY (codaula, codclasse)
                                  REFERENCES aula(codaula, codclasse)
                                  ON DELETE CASCADE
                                  ON UPDATE CASCADE
) ENGINE=InnoDB;


-- ==========================================
-- TABELA: anotacao
--
-- TURMA -> relacionada a uma classe
-- ALUNO -> relacionada a um aluno
-- AULA  -> relacionada a uma aula
-- ==========================================

CREATE TABLE anotacoes (
                           codanotacao BIGINT AUTO_INCREMENT PRIMARY KEY,
                           tipo VARCHAR(20) NOT NULL,
                           texto TEXT NOT NULL,

                           codclasse BIGINT NULL,
                           codaluno BIGINT NULL,
                           codaula BIGINT NULL,

                           CONSTRAINT fk_anotacoes_classe
                               FOREIGN KEY (codclasse)
                                   REFERENCES classe(codclasse)
                                   ON DELETE CASCADE
                                   ON UPDATE CASCADE,

                           CONSTRAINT fk_anotacoes_aluno
                               FOREIGN KEY (codaluno)
                                   REFERENCES aluno(codaluno)
                                   ON DELETE CASCADE
                                   ON UPDATE CASCADE,

                           CONSTRAINT fk_anotacoes_aula
                               FOREIGN KEY (codaula)
                                   REFERENCES aula(codaula)
                                   ON DELETE CASCADE
                                   ON UPDATE CASCADE
) ENGINE=InnoDB;


-- ==========================================
-- ÍNDICES
-- ==========================================

CREATE INDEX idx_aluno_codclasse
    ON aluno(codclasse);

CREATE INDEX idx_aula_codclasse_data
    ON aula(codclasse, diahora);

CREATE INDEX idx_horario_codclasse_dia_hora
    ON horario(codclasse, diasemana, horainicio);

CREATE INDEX idx_anotacoes_classe
    ON anotacoes(codclasse);

CREATE INDEX idx_anotacoes_aluno
    ON anotacoes(codaluno);

CREATE INDEX idx_anotacoes_aula
    ON anotacoes(codaula);