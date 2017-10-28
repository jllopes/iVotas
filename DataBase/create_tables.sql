create database iVotas;

use iVotas;

create table faculty
	(id int(4) not null auto_increment, 
    name varchar(25),
	primary key (id)
    );

create table department
	( id int(4) not null auto_increment,
    id_faculty int(4) not null,
    name varchar(25),
    primary key(id),
	foreign key(id_faculty) references faculty(id)
	);	



create table person
	(id int(4) not null auto_increment,
    username varchar(16) not null,
    password varchar(16) not null,
    type int(1) not null,
	id_faculty int(4) not null,
    id_department int(4),
    primary key (id),
    foreign key(id_faculty) references faculty(id),
    foreign key(id_department) references department(id),
    UNIQUE(username),
    constraint type_check_1_0 check (type is not null or type=1 or type=2 or type=3)
    );
    
create table data_person
	(
    id int(4) not null auto_increment,
    name varchar(40),
    address varchar(100),
    cc_number int(9),
    cc_month int(2),
    cc_year int(4),
	id_person int(4) not null,
    phoneNumber varchar(13) not null,
    primary key(id),
    foreign key(id_person) references person(id),
    UNIQUE(id_person),
    constraint valid_year check (cc_year > 1970 and cc_year < 2050),
    constraint valid_month check (cc_month > 0 and cc_month < 13)
    );
    

create table election
	(
    id int(4) not null auto_increment,
	name varchar(50) not null,
    description varchar(100),
    start_date datetime default current_timestamp,
    end_date datetime not null,
    department_number int(4), #numero department
    vote_blank int(4) default 0,
    vote_null int(4) default 0,
    primary key(id),
    constraint after_date check (start_date < end_date and start_date >= current_timestamp)
	);
    
create table vote_table
	(id int(4) not null auto_increment,
    id_department int(4) not null,
    primary key(id),
    foreign key(id_department) references department(id)
	#UNIQUE(id_department)
    );
    
create table list_election
	(
    id int(4) not null auto_increment,
    name varchar(10) not null,
	id_election int(4) not null,
    type int(1) not null,
   	vote int(4) default 0,
    primary key(id),
    foreign key(id_election) references election(id),
	constraint type_check_1_0 check (type is not null or type=1 or type=2 or type=3)
    );

create table person_list
	(
	id int(4) not null auto_increment,
	id_person int(4) not null,
    id_list int(4) not null,
    primary key(id),
	foreign key(id_list) references list_election(id),
	foreign key(id_person) references person(id)
    );

    
create table vote
	(
    id int(4) not null auto_increment,
	id_election int(4) not null,
	id_table int(4) not null,
	id_person int(4) not null,
    time_vote TIMESTAMP default current_timestamp,
    primary key(id),
    foreign key(id_election) references election(id),
    foreign key(id_table) references vote_table(id),
    foreign key(id_person) references person(id)
    );

create index index_election on vote(id_election);
