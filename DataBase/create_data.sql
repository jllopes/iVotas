#5 eleitores
#3 eleiçoes, 1 por terminar


insert into faculty(name) values ("FCTUC");
insert into faculty(name) values ("FLUC");

insert into departement(id_faculty, name) values (1, "DEI");
insert into departement(id_faculty, name) values (1, "DEM");
insert into departement(id_faculty, name) values (2, "Portuges");
insert into departement(id_faculty, name) values (2, "Ingles");

insert into vote_table(name, id_departement) values("NEI",1);
insert into vote_table(name, id_departement) values("NEM",2);
insert into vote_table(name, id_departement) values("NPORTUGUES",3);
insert into vote_table(name, id_departement) values("NINGLES",4);



insert into person(username, password, type, id_faculty, id_departement) values ("gbc","gbc",1,1,1);
insert into data_person(name, address, cc_number, cc_month, cc_year, id_person) values ("Gabriel Cardoso", "Coimbra", 123123123, 10, 1990, 1 );
insert into person(username, password, type, id_faculty, id_departement) values ("jonny","jonny",1,1,1);
insert into data_person(name, address, cc_number, cc_month, cc_year, id_person) values ("Joao Lopes", "Coimbra", 123123123, 8, 1990, 2 );

insert into person(username, password, type, id_faculty) values ("maria","maria",2,2);
insert into data_person(name, address, cc_number, cc_month, cc_year, id_person) values ("Professora Maria Joana", "Coimbra", 123123123, 10, 1990, 3 );
insert into person(username, password, type, id_faculty, id_departement) values ("djoana","djoana",2,2,4);
insert into data_person(name, address, cc_number, cc_month, cc_year, id_person) values ("Dona Joana Silva", "Coimbra", 123123123, 10, 1990, 4 );

insert into election(name,description,start_date, end_date, departement_number) values ("Votaçao nei","nova votaçao para o nucleo informatica",'2017-10-15 12:00:00','2017-10-17 12:00:00',1);
insert into list_election(name, id_election, type) values ("Lista A",1,1);
insert into person_list(id_person, id_list) values (1,1);
insert into list_election(name, id_election, type) values ("Lista B",1,1);
insert into person_list(id_person, id_list) values (2,2);

insert into vote(id_election, id_person, id_table) values (1,1,1);
insert into vote(id_election, id_person, id_table) values (1,2,1);
update list_election set list_election.vote = list_election.vote +1 where list_election.id = 1;
update list_election set list_election.vote = list_election.vote +1 where list_election.id = 1;

/*
select * from data_person;
select * from faculty;
select * from departement;
select * from person;
*/