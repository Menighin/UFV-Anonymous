CREATE DATABASE `unichat` DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci;
USE `unichat`;

CREATE TABLE IF NOT EXISTS universities (
	id INTEGER AUTO_INCREMENT,
	name VARCHAR(100) NOT NULL,
	acronym VARCHAR (15) NOT NULL,
	email VARCHAR(50) NOT NULL,
CONSTRAINT PK_universities PRIMARY KEY(id, name)
);

CREATE TABLE IF NOT EXISTS courses (
	id INTEGER AUTO_INCREMENT,
	university_id INTEGER NOT NULL,
	name VARCHAR(40) NOT NULL,
CONSTRAINT PK_courses PRIMARY KEY (id, name),
CONSTRAINT FK_courses FOREIGN KEY (university_id) REFERENCES universities (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;

CREATE TABLE IF NOT EXISTS users (
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
	logged TINYINT(1) DEFAULT 0,
	special TINYINT(1) DEFAULT 0,
CONSTRAINT PK_users PRIMARY KEY (id, username, email),
CONSTRAINT FK_users_University FOREIGN KEY (university) REFERENCES universities (id),
CONSTRAINT FK_users_Cousers FOREIGN KEY (course) REFERENCES courses (id)
);

CREATE TABLE IF NOT EXISTS conversations (
	id INTEGER AUTO_INCREMENT, 
	user1 INTEGER NOT NULL,
	user2 INTEGER DEFAULT NULL,
	u1wantssex SET('m','f','w') NOT NULL,
	u1wantscourse INTEGER NULL,
	ready TINYINT(1) NOT NULL,
	participants INTEGER NOT NULL,
	started_on DATETIME NOT NULL,
CONSTRAINT PK_conversations PRIMARY KEY (id),
CONSTRAINT FK_conversations_users_1 FOREIGN KEY (user1) REFERENCES users (id),
CONSTRAINT FK_conversations_users_2 FOREIGN KEY (user2) REFERENCES users (id),
CONSTRAINT FK_conversations_courses FOREIGN KEY (u1wantscourse) REFERENCES courses (id)
);

CREATE TABLE IF NOT EXISTS messages (
	id INTEGER AUTO_INCREMENT,
	conversation_id INTEGER NOT NULL,
	message TEXT NOT NULL,
	time DATETIME NOT NULL,
	author INTEGER NOT NULL,
	is_read TINYINT(1) NOT NULL,
	END_FLAG TINYINT(1) NOT NULL,
CONSTRAINT PK_messages PRIMARY KEY (id),
CONSTRAINT FK_messages_Conversation FOREIGN KEY (conversation_id) REFERENCES conversations (id)
);

/* INSERTs */
/* universities and courses */
INSERT INTO universities (name, email, acronym) VALUES ('Universidade Federal de Viçosa', '@ufv.br', 'UFV');

INSERT INTO courses (university_id, name) VALUES (1, 'Administração');
INSERT INTO courses (university_id, name) VALUES (1, 'Agronomia');
INSERT INTO courses (university_id, name) VALUES (1, 'Arquitetura e Urbanismo');
INSERT INTO courses (university_id, name) VALUES (1, 'Bioquímica');
INSERT INTO courses (university_id, name) VALUES (1, 'Ciência da Computação');
INSERT INTO courses (university_id, name) VALUES (1, 'Ciência e Tecnologia de Laticínios');
INSERT INTO courses (university_id, name) VALUES (1, 'Ciências Biológicas');
INSERT INTO courses (university_id, name) VALUES (1, 'Ciências Contábeis');
INSERT INTO courses (university_id, name) VALUES (1, 'Ciências Econômicas');
INSERT INTO courses (university_id, name) VALUES (1, 'Ciências Sociais');
INSERT INTO courses (university_id, name) VALUES (1, 'Comunicação Social');
INSERT INTO courses (university_id, name) VALUES (1, 'Cooperativismo');
INSERT INTO courses (university_id, name) VALUES (1, 'Dança');
INSERT INTO courses (university_id, name) VALUES (1, 'Direito');
INSERT INTO courses (university_id, name) VALUES (1, 'Economia Doméstica');
INSERT INTO courses (university_id, name) VALUES (1, 'Educação Física');
INSERT INTO courses (university_id, name) VALUES (1, 'Educação Infantil');
INSERT INTO courses (university_id, name) VALUES (1, 'Enfermagem');
INSERT INTO courses (university_id, name) VALUES (1, 'Engenharia Agrícola e Ambiental');
INSERT INTO courses (university_id, name) VALUES (1, 'Engenharia Ambiental');
INSERT INTO courses (university_id, name) VALUES (1, 'Engenharia Civil');
INSERT INTO courses (university_id, name) VALUES (1, 'Engenharia de Agrimensura e Cartográfica');
INSERT INTO courses (university_id, name) VALUES (1, 'Engenharia de Alimentos');
INSERT INTO courses (university_id, name) VALUES (1, 'Engenharia de Produção');
INSERT INTO courses (university_id, name) VALUES (1, 'Engenharia Elétrica');
INSERT INTO courses (university_id, name) VALUES (1, 'Engenharia Florestal');
INSERT INTO courses (university_id, name) VALUES (1, 'Engenharia Mecânica');
INSERT INTO courses (university_id, name) VALUES (1, 'Engenharia Química');
INSERT INTO courses (university_id, name) VALUES (1, 'Física');
INSERT INTO courses (university_id, name) VALUES (1, 'Geografia');
INSERT INTO courses (university_id, name) VALUES (1, 'História');
INSERT INTO courses (university_id, name) VALUES (1, 'Letras');
INSERT INTO courses (university_id, name) VALUES (1, 'Licenciatura em Ciências Biológicas');
INSERT INTO courses (university_id, name) VALUES (1, 'Licenciatura em Física');
INSERT INTO courses (university_id, name) VALUES (1, 'Licenciatura em Matemática');
INSERT INTO courses (university_id, name) VALUES (1, 'Licenciatura em Química');
INSERT INTO courses (university_id, name) VALUES (1, 'Matemática');
INSERT INTO courses (university_id, name) VALUES (1, 'Medicina');
INSERT INTO courses (university_id, name) VALUES (1, 'Medicina Veterinária');
INSERT INTO courses (university_id, name) VALUES (1, 'Nutrição');
INSERT INTO courses (university_id, name) VALUES (1, 'Pedagogia');
INSERT INTO courses (university_id, name) VALUES (1, 'Química');
INSERT INTO courses (university_id, name) VALUES (1, 'Secreteriado Executivo Trilingue');
INSERT INTO courses (university_id, name) VALUES (1, 'Zootecnia');

/* users */
INSERT INTO users (username, password, api_key, email, sex, university, course, valid, hash, last_seen, special) VALUES ('menighin', '12345', 'TesteAPIKey', 'joao.menighin@ufv.br', 'm', 1, 5, 1, 'TesteHASH', NULL, 1);
INSERT INTO users (username, password, api_key, email, sex, university, course, valid, hash, last_seen, special) VALUES ('aline', '12345', 'TesteAPIKey2', 'aline@ufv.br', 'm', 1, 11, 1, 'TesteHASH', NULL, 0);
