create table faculdade
	(id int(4) not null auto_increment, 
    nome varchar(25),
	primary key (id)
    );

create table departamento
	( id int(4) not null auto_increment,
    nome varchar(25),
    primary key(id),
	foreign key(id_faculdade) references faculdade(id)
	);	

create table mesa_de_voto
	( id int(4) not null auto_increment,
    nome varchar(25),
    primary key(id),
    foreign key(id_departamento) references departamento(id)
    );


create table pessoa
	(id int(4) not null auto_increment,
    username varchar(16) not null,
    password varchar(16) not null,
    tipo int(1) not null,
    primary key (id),
    foreign key(id_faculdade) references faculdade(id),
    foreign key(id_departamento) references departamento(id),
    constraint tipo_check_1_0 check (tipo is not null or tipo=1 or tipo=2 or tipo=3)
    );
    
create table dados_pessoa
	(
    id int(4) not null auto_increment,
    nome varchar(40),
    morada varchar(100),
    telefone int(9),
    mes_cc int(2),
    ano_cc int(4),
    foreign key(id_pessoa) references pessoa(id),
    constraint ano_valido check (ano_cc > 1970 and ano_cc < 2050),
    constraint mes_valido check (mes_cc > 0 and mes_cc < 13)
    );
    

create table eleicao
	(
    id int(4) not null auto_increment,
    data_inicio datetime default current_timestamp,
    data_fim datetime not null,
    titulo varchar(50) not null,
    descricao varchar(100),
    primary key(id),
    constraint after_date check (data_inicio < data_fim and data_inicio >= current_timestamp)
	);
    
create table lista
	(
    id int(4) not null auto_increment,
    nome varchar(10) not null,
    primary key(id),
    foreign key(id_eleicao) references leilao(id)
    );

create table pessoa_da_lista
	(
    foreign key(id_lista) references lista(id),
    foreign key(id_pessoa) references pessoa(id)
    );
    
create table lista_eleicao
	(
	totalvotos int(4) default 0,
	foreign key(id_lista) references lista(id)
    );
    
create table voto
	(
    id int(4) not null auto_increment,
    foreign key(id_eleicao) references eleicao(id),
    foreign key(id_mesa) references mesa_de_voto(id),
    foreign key(id_pessoa) references pessoa(id)
    );
