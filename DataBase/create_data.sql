#5 eleitores
#3 eleiçoes, 1 por terminar


insert into faculty(name) values ("FCTUC");
insert into faculty(name) values ("FLUC");

insert into department(faculty, name) values (1, "DEI");
insert into department(faculty, name) values (1, "DEM");
insert into department(faculty, name) values (2, "Portuges");
insert into department(faculty, name) values (2, "Ingles");


insert into votetable() values ();


insert into user(username, password, type, faculty, department, name, address, ccNumber, ccMonth, ccYear, phoneNumber) values ("pjsg","pjsg",1,1,1,"Pedro Gonçalves", "Coimbra", 123123123, 10, 1990,"+351123456789");
insert into user(username, password, type, faculty, department, name, address, ccNumber, ccMonth, ccYear, phoneNumber) values ("jllopes","jllopes",1,1,1,"Joao Lopes", "Coimbra", 123123123, 8, 1990,"+351123456789");
insert into user(username, password, type, faculty, name, address, ccNumber, ccMonth, ccYear,phoneNumber) values ("maria","maria",2,2,"Professora Maria Joana", "Coimbra", 123123123, 10, 1990,"+351123456789");
insert into user(username, password, type, faculty, department, name, address, ccNumber, ccMonth, ccYear,phoneNumber) values ("djoana","djoana",2,2,4,"Dona Joana Silva", "Coimbra", 123123123, 10, 1990,"+351123456789");
insert into user(username, password, type, faculty, department, name, address, ccNumber, ccMonth, ccYear,phoneNumber) values ("docente5","docente5",2,1,1,"Mais uma pessoa porque sim", "Russia", 123123123, 8, 1990,"+351123456789");


#eleiçao 1 com 2 listas
insert into election(name,description,startDate, endDate, department) values ("Votaçao nei","nova votaçao para o nucleo informatica",'2017-10-15 12:00:00','2017-10-17 12:00:00',1);
insert into voteTable( department) values(1);
insert into voteTable( department) values(2);
insert into voteTable( department) values(3);
insert into voteTable( department) values(4);
insert into electionList(name, election, type) values ("Lista A",1,1);
insert into candidate(user, list) values (1,1);
insert into electionList(name, election, type) values ("Lista B",1,1);
insert into candidate(user, list) values (2,2);

insert into vote(election, user, voteTable) values (1,1,2);
insert into vote(election, user, voteTable) values (1,2,2);
update electionList set electionList.vote = electionList.vote +1 where electionList.id = 1;
update electionList set electionList.vote = electionList.vote +1 where electionList.id = 1;

#eleiçao 2 - 0 votos
insert into election(name,description,startDate, endDate, department) values ("Votaçao DG","votaçao geral",'2017-10-15 12:00:00','2017-10-17 12:00:00',0);
insert into electionList(name, election, type) values ("Lista D",2,2);
insert into candidate(user, list) values (3,3);
insert into electionList(name, election, type) values ("Lista F",2,3);
insert into candidate(user, list) values (4,4);

#eleicao 3
insert into election(name,description, endDate, department) values ("Votaçao live","eleicao q deve estar a decorrer",date_add(current_timestamp(), interval 1 day) ,1);
insert into voteTable( department) values(1);
insert into voteTable( department) values(2);
insert into voteTable( department) values(3);
insert into voteTable( department) values(4);

insert into electionList(name, election, type) values ("Lista A",3,1);
insert into candidate(user, list) values (1,5);
insert into electionList(name, election, type) values ("Lista B",3,1);
insert into candidate(user, list) values (5,6);

insert into vote(election, user, voteTable) values (3,5,6);
update electionList set electionList.vote = electionList.vote +1 where electionList.id = 5;
update electionList set electionList.vote = electionList.vote +1 where electionList.id = 6;
insert into department(faculty, name) values (1, "novo");

insert into user(username, password, type, faculty, department, name, address, ccNumber, ccMonth, ccYear,phoneNumber) values ("admin","admin",4,1,1,"Admin", "Admin", 999999999, 8, 1990,"+351123456789");
/*
select * from election where startDate < current_timestamp() and endDate > current_timestamp();
select * from electionList;

select * from election;
select * from user ;
select * from data_user;
select * from faculty;
select * from user;
select * from candidate;
*/


