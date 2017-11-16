create database iVotas;

use iVotas;

create table faculty
	(id int(4) not null auto_increment, 
    name varchar(25),
	primary key (id)
    );

create table department
	( id int(4) not null auto_increment,
    faculty int(4) not null,
    name varchar(25),
    primary key(id),
	foreign key(faculty) references faculty(id)
	);	



create table user
	(id int(4) not null auto_increment,
    username varchar(16) not null,
    password varchar(16) not null,
    type int(1) not null,
	faculty int(4) not null,
    department int(4),
    name varchar(40),
    address varchar(100),
    ccNumber int(9),
    ccMonth int(2),
    ccYear int(4),
    phoneNumber varchar(13) not null,
    primary key (id),
    foreign key(faculty) references faculty(id),
    foreign key(department) references department(id),
    UNIQUE(username),
    constraint validYear check (cc_year > 1970 and cc_year < 2050),
    constraint validMonth check (cc_month > 0 and cc_month < 13),
    constraint typeCheck check (type is not null or type=1 or type=2 or type=3)
    );

create table election
	(
    id int(4) not null auto_increment,
	name varchar(50) not null,
    description varchar(100),
    startDate datetime default current_timestamp,
    endDate datetime not null,
    department int(4), #numero department
    blankVotes int(4) default 0,
    nullVotes int(4) default 0,
    primary key(id),
    constraint afterDate check (startDate < endDate and startDate >= current_timestamp)
	);
    
create table voteTable
	(id int(4) not null auto_increment,
    department int(4) not null,
    primary key(id),
    foreign key(department) references department(id)
	#UNIQUE(id_department)
    );
    
create table electionList
	(
    id int(4) not null auto_increment,
    name varchar(10) not null,
	election int(4) not null,
    type int(1) not null,
   	vote int(4) default 0,
    primary key(id),
    foreign key(election) references election(id),
	constraint typeCheck check (type is not null or type=1 or type=2 or type=3)
    );

create table candidate
	(
	id int(4) not null auto_increment,
	user int(4) not null,
    list int(4) not null,
    primary key(id),
	foreign key(list) references electionList(id),
	foreign key(user) references user(id)
    );

    
create table vote
	(
    id int(4) not null auto_increment,
	election int(4) not null,
	voteTable int(4) not null,
	user int(4) not null,
    voteTime TIMESTAMP default current_timestamp,
    primary key(id),
    foreign key(election) references election(id),
    foreign key(voteTable) references voteTable(id),
    foreign key(user) references user(id)
    );

create index electionIndex on vote(election);
