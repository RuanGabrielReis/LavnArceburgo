ALTER TABLE aula
    ADD CONSTRAINT uk_aula_classe_diahora
        UNIQUE (codclasse, diahora);