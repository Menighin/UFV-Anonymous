CREATE DATABASE `UniChat` DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci;
USE `UniChat`;

CREATE TABLE IF NOT EXISTS Universities (
	id INTEGER AUTO_INCREMENT,
	name VARCHAR(100) NOT NULL,
	email VARCHAR(50) NOT NULL,
CONSTRAINT PK_Universities PRIMARY KEY(id, name)
);

CREATE TABLE IF NOT EXISTS Courses (
	id INTEGER AUTO_INCREMENT,
	university_id INTEGER NOT NULL,
	name VARCHAR(40) NOT NULL,
CONSTRAINT PK_Courses PRIMARY KEY (id, name),
CONSTRAINT FK_Courses FOREIGN KEY (university_id) REFERENCES Universities (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;

CREATE TABLE IF NOT EXISTS Users (
	id INTEGER AUTO_INCREMENT,
	username VARCHAR(25) NOT NULL,
	password VARCHAR(25) NOT NULL,
	api_key VARCHAR(50) NOT NULL,
	email VARCHAR(60) NOT NULL,
	sex SET('m', 'f') NOT NULL,
	university INTEGER NOT NULL,
	course INTEGER NOT NULL,
	valid TINYINT(1) NOT NULL,
	hash VARCHAR(50) NOT NULL,
	last_seen DATETIME DEFAULT NULL,
	logged TINYINT(1) DEFAULT NULL,
CONSTRAINT PK_Users PRIMARY KEY (id, username, email),
CONSTRAINT FK_Users_University FOREIGN KEY (university) REFERENCES Universities (id),
CONSTRAINT FK_Users_Cousers FOREIGN KEY (course) REFERENCES Courses (id)
);

CREATE TABLE IF NOT EXISTS Conversations (
	id INTEGER AUTO_INCREMENT, 
	user1 INTEGER NOT NULL,
	user2 INTEGER DEFAULT NULL,
	u1wantssex SET('m','f','w') NOT NULL,
	u1wantscourse INTEGER NULL,
	ready TINYINT(1) NOT NULL,
	participants INTEGER NOT NULL,
	started_on DATETIME NOT NULL,
CONSTRAINT PK_Conversations PRIMARY KEY (id),
CONSTRAINT FK_Conversations_Users_1 FOREIGN KEY (user1) REFERENCES Users (id),
CONSTRAINT FK_Conversations_Users_2 FOREIGN KEY (user2) REFERENCES Users (id),
CONSTRAINT FK_Conversations_Courses FOREIGN KEY (u1wantscourse) REFERENCES Courses (id)
);

CREATE TABLE IF NOT EXISTS Messages (
	id INTEGER AUTO_INCREMENT,
	conversation_id INTEGER NOT NULL,
	message TEXT NOT NULL,
	time DATETIME NOT NULL,
	author INTEGER NOT NULL,
	is_read TINYINT(1) NOT NULL,
	END_FLAG TINYINT(1) NOT NULL,
CONSTRAINT PK_Messages PRIMARY KEY (id),
CONSTRAINT FK_Messages_Conversation FOREIGN KEY (conversation_id) REFERENCES Conversations (id)
);

/* INSERTs */
/* Universities and courses */
INSERT INTO Universities (name, email) VALUES ('Universidade Federal de Viçosa', '@ufv.br');

INSERT INTO Courses (university_id, name) VALUES (1, 'Administração');
INSERT INTO Courses (university_id, name) VALUES (1, 'Agronomia');
INSERT INTO Courses (university_id, name) VALUES (1, 'Arquitetura e Urbanismo');
INSERT INTO Courses (university_id, name) VALUES (1, 'Bioquímica');
INSERT INTO Courses (university_id, name) VALUES (1, 'Ciência da Computação');
INSERT INTO Courses (university_id, name) VALUES (1, 'Ciência e Tecnologia de Laticínios');
INSERT INTO Courses (university_id, name) VALUES (1, 'Ciências Biológicas');
INSERT INTO Courses (university_id, name) VALUES (1, 'Ciências Contábeis');
INSERT INTO Courses (university_id, name) VALUES (1, 'Ciências Econômicas');
INSERT INTO Courses (university_id, name) VALUES (1, 'Ciências Sociais');
INSERT INTO Courses (university_id, name) VALUES (1, 'Comunicação Social');
INSERT INTO Courses (university_id, name) VALUES (1, 'Cooperativismo');
INSERT INTO Courses (university_id, name) VALUES (1, 'Dança');
INSERT INTO Courses (university_id, name) VALUES (1, 'Direito');
INSERT INTO Courses (university_id, name) VALUES (1, 'Economia Doméstica');
INSERT INTO Courses (university_id, name) VALUES (1, 'Educação Física');
INSERT INTO Courses (university_id, name) VALUES (1, 'Educação Infantil');
INSERT INTO Courses (university_id, name) VALUES (1, 'Enfermagem');
INSERT INTO Courses (university_id, name) VALUES (1, 'Engenharia Agrícola e Ambiental');
INSERT INTO Courses (university_id, name) VALUES (1, 'Engenharia Ambiental');
INSERT INTO Courses (university_id, name) VALUES (1, 'Engenharia Civil');
INSERT INTO Courses (university_id, name) VALUES (1, 'Engenharia de Agrimensura e Cartográfica');
INSERT INTO Courses (university_id, name) VALUES (1, 'Engenharia de Alimentos');
INSERT INTO Courses (university_id, name) VALUES (1, 'Engenharia de Produção');
INSERT INTO Courses (university_id, name) VALUES (1, 'Engenharia Elétrica');
INSERT INTO Courses (university_id, name) VALUES (1, 'Engenharia Florestal');
INSERT INTO Courses (university_id, name) VALUES (1, 'Engenharia Mecânica');
INSERT INTO Courses (university_id, name) VALUES (1, 'Engenharia Química');
INSERT INTO Courses (university_id, name) VALUES (1, 'Física');
INSERT INTO Courses (university_id, name) VALUES (1, 'Geografia');
INSERT INTO Courses (university_id, name) VALUES (1, 'História');
INSERT INTO Courses (university_id, name) VALUES (1, 'Letras');
INSERT INTO Courses (university_id, name) VALUES (1, 'Licenciatura em Ciências Biológicas');
INSERT INTO Courses (university_id, name) VALUES (1, 'Licenciatura em Física');
INSERT INTO Courses (university_id, name) VALUES (1, 'Licenciatura em Matemática');
INSERT INTO Courses (university_id, name) VALUES (1, 'Licenciatura em Química');
INSERT INTO Courses (university_id, name) VALUES (1, 'Matemática');
INSERT INTO Courses (university_id, name) VALUES (1, 'Medicina');
INSERT INTO Courses (university_id, name) VALUES (1, 'Medicina Veterinária');
INSERT INTO Courses (university_id, name) VALUES (1, 'Nutrição');
INSERT INTO Courses (university_id, name) VALUES (1, 'Pedagogia');
INSERT INTO Courses (university_id, name) VALUES (1, 'Química');
INSERT INTO Courses (university_id, name) VALUES (1, 'Secreteriado Executivo Trilingue');
INSERT INTO Courses (university_id, name) VALUES (1, 'Zootecnia');

/* Users */
INSERT INTO Users (username, password, api_key, email, sex, university, course, valid, hash, last_seen) VALUES ('menighin', '12345', 'TesteAPIKey', 'joao.menighin@ufv.br', 'm', 1, 3, 1, 'TesteHASH', NULL);
