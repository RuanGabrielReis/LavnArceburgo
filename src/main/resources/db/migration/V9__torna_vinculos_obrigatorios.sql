ALTER TABLE aluno
DROP FOREIGN KEY fk_aluno_classe;

ALTER TABLE aluno
    MODIFY COLUMN codclasse BIGINT NOT NULL;

ALTER TABLE aluno
    ADD CONSTRAINT fk_aluno_classe
        FOREIGN KEY (codclasse)
            REFERENCES classe(codclasse)
            ON DELETE RESTRICT;

ALTER TABLE classe
DROP FOREIGN KEY fk_classe_professor;

ALTER TABLE classe
    MODIFY COLUMN codprofessor BIGINT NOT NULL;

ALTER TABLE classe
    ADD CONSTRAINT fk_classe_professor
        FOREIGN KEY (codprofessor)
            REFERENCES funcionario(codfuncionario)
            ON DELETE RESTRICT;

ALTER TABLE horario
    MODIFY COLUMN sala VARCHAR(255) NOT NULL;