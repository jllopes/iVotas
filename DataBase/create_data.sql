#5 eleitores
#3 eleiçoes, 1 por terminar


insert into faculty(name) values ("FCTUC");
insert into faculty(name) values ("FLUC");

insert into department(id_faculty, name) values (1, "DEI");
insert into department(id_faculty, name) values (1, "DEM");
insert into department(id_faculty, name) values (2, "Portuges");
insert into department(id_faculty, name) values (2, "Ingles");





insert into person(username, password, type, id_faculty, id_department) values ("gbc","gbc",1,1,1);
insert into data_person(name, address, cc_number, cc_month, cc_year, phoneNumber,id_person) values ("Gabriel Cardoso", "Coimbra", 123123123, 10, 1990,"+351123456789", 1 );
insert into person(username, password, type, id_faculty, id_department) values ("jonny","jonny",1,1,1);
insert into data_person(name, address, cc_number, cc_month, cc_year, phoneNumber,id_person) values ("Joao Lopes", "Coimbra", 123123123, 8, 1990,"+351123456789", 2 );

insert into person(username, password, type, id_faculty) values ("maria","maria",2,2);
insert into data_person(name, address, cc_number, cc_month, cc_year,phoneNumber, id_person) values ("Professora Maria Joana", "Coimbra", 123123123, 10, 1990,"+351123456789", 3 );
insert into person(username, password, type, id_faculty, id_department) values ("djoana","djoana",2,2,4);
insert into data_person(name, address, cc_number, cc_month, cc_year,phoneNumber, id_person) values ("Dona Joana Silva", "Coimbra", 123123123, 10, 1990,"+351123456789", 4 );


insert into person(username, password, type, id_faculty, id_department) values ("docente5","docente5",2,1,1);
insert into data_person(name, address, cc_number, cc_month, cc_year,phoneNumber, id_person) values ("Mais uma pessoa porque sim", "Russia", 123123123, 8, 1990,"+351123456789", 5 );


#eleiçao 1 com 2 listas
insert into election(name,description,start_date, end_date, department_number) values ("Votaçao nei","nova votaçao para o nucleo informatica",'2017-10-15 12:00:00','2017-10-17 12:00:00',1);
insert into vote_table( id_department) values(1);
insert into vote_table( id_department) values(2);
insert into vote_table( id_department) values(3);
insert into vote_table( id_department) values(4);
insert into list_election(name, id_election, type) values ("Lista A",1,1);
insert into person_list(id_person, id_list) values (1,1);
insert into list_election(name, id_election, type) values ("Lista B",1,1);
insert into person_list(id_person, id_list) values (2,2);

insert into vote(id_election, id_person, id_table) values (1,1,1);
insert into vote(id_election, id_person, id_table) values (1,2,1);
update list_election set list_election.vote = list_election.vote +1 where list_election.id = 1;
update list_election set list_election.vote = list_election.vote +1 where list_election.id = 1;

#eleiçao 2 - 0 votos
insert into election(name,description,start_date, end_date, department_number) values ("Votaçao DG","votaçao geral",'2017-10-15 12:00:00','2017-10-17 12:00:00',0);
insert into list_election(name, id_election, type) values ("Lista D",2,2);
insert into person_list(id_person, id_list) values (3,3);
insert into list_election(name, id_election, type) values ("Lista F",2,3);
insert into person_list(id_person, id_list) values (4,4);

#eleicao 3
insert into election(name,description, end_date, department_number) values ("Votaçao live","eleicao q deve estar a decorrer",date_add(current_timestamp(), interval 1 day) ,1);
insert into vote_table( id_department) values(1);
insert into vote_table( id_department) values(2);
insert into vote_table( id_department) values(3);
insert into vote_table( id_department) values(4);

insert into list_election(name, id_election, type) values ("Lista A",3,1);
insert into person_list(id_person, id_list) values (1,5);
insert into list_election(name, id_election, type) values ("Lista B",3,2);
insert into person_list(id_person, id_list) values (5,6);

insert into vote(id_election, id_person, id_table) values (3,2,5);
insert into vote(id_election, id_person, id_table) values (3,5,5);
update list_election set list_election.vote = list_election.vote +1 where list_election.id = 5;
update list_election set list_election.vote = list_election.vote +1 where list_election.id = 6;
insert into department(id_faculty, name) values (1, "novo");
#select vote.time_vote, person.username, election.name, department.name from vote, person,election, vote_table, department where vote.id_person = 1 and vote.id_election = 1 and vote.id_person = person.id and vote.id_election = election.id  and vote.id_table=vote_table.id and vote_table.id_department = department.id;

insert into list_election(name, id_election, type) values ("Lista A",4,1);

select * from list_election;
select election.id , election.name from election, list_election  where (election.start_date < current_timestamp() and election.end_date > current_timestamp() and (election.department_number = 1 or election.department_number = 0) and  list_election.type= 1 and list_election.id_election = election.id ) group by list_election.id_election;
select* from vote;
select * from election;
/*
select * from election where start_date < current_timestamp() and end_date > current_timestamp();
select * from list_election;

select * from election;
select * from person ;
select * from data_person;
select * from faculty;
select * from person;
select * from person_list;
*/
