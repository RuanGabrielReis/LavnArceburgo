
-- senha: professor123
UPDATE funcionario f
    JOIN usuario u ON u.codusuario = f.codfuncionario
    SET f.senha = '$2a$10$OO1Oq84P9Lq.W9xKZU5nS.WlD9n2aHlxrd1FIbHrljbSDqGZIgSSa'
WHERE u.email = 'professor@lavn.com';
-- senha: secretaria123
UPDATE funcionario f
    JOIN usuario u ON u.codusuario = f.codfuncionario
    SET f.senha = '$2a$10$sODTbZIzwR5AN04dv6JcROoSiogedNbFCBGZLmRjCjh5RLkKIQMoa'
WHERE u.email = 'secretaria@lavn.com';