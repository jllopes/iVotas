#5 eleitores
#3 eleiçoes, 1 por terminar


insert into faculdade(nome) values ("FCTUC");
insert into faculdade(nome) values ("FLUC");

insert into departamento(id_faculdade, nome) values (1, "DEI");
insert into departamento(id_faculdade, nome) values (1, "DEM");
insert into departamento(id_faculdade, nome) values (2, "Portuges");
insert into departamento(id_faculdade, nome) values (2, "Ingles");

insert into pessoa(username, password, tipo, id_faculdade, id_departamento) values ("gbc","gbc",1,1,1);
insert into dados_pessoa(nome, morada, telefone, mes_cc, ano_cc, id_pessoa) values ("Gabriel Cardoso", "Coimbra", 123123123, 10, 1990, 1 );
insert into pessoa(username, password, tipo, id_faculdade, id_departamento) values ("jonny","jonny",1,1,1);
insert into dados_pessoa(nome, morada, telefone, mes_cc, ano_cc, id_pessoa) values ("Joao Lopes", "Coimbra", 123123123, 8, 1990, 2 );

insert into pessoa(username, password, tipo, id_faculdade) values ("maria","maria",2,2);
insert into dados_pessoa(nome, morada, telefone, mes_cc, ano_cc, id_pessoa) values ("Professora Maria Joana", "Coimbra", 123123123, 10, 1990, 3 );
insert into pessoa(username, password, tipo, id_faculdade, id_departamento) values ("djoana","djoana",2,2,4);
insert into dados_pessoa(nome, morada, telefone, mes_cc, ano_cc, id_pessoa) values ("Dona Joana Silva", "Coimbra", 123123123, 10, 1990, 4 );

/*

select * from faculdade;
select * from departamento;
select * from pessoa;
select * from dados_pessoa;
*/