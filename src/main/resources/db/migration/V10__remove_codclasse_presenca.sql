ALTER TABLE presenca
DROP FOREIGN KEY fk_presenca_aluno_classe;

ALTER TABLE presenca
DROP FOREIGN KEY fk_presenca_aula_classe;


ALTER TABLE presenca
    ADD CONSTRAINT fk_presenca_aluno
        FOREIGN KEY (codaluno)
            REFERENCES aluno(codaluno)
            ON DELETE CASCADE
            ON UPDATE CASCADE;

ALTER TABLE presenca
    ADD CONSTRAINT fk_presenca_aula
        FOREIGN KEY (codaula)
            REFERENCES aula(codaula)
            ON DELETE CASCADE
            ON UPDATE CASCADE;


ALTER TABLE presenca
DROP COLUMN codclasse;