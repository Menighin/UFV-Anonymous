CREATE DATABASE `unichat` DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci;
USE `unichat`;

CREATE TABLE IF NOT EXISTS universities (
	id INTEGER AUTO_INCREMENT UNIQUE,
	name VARCHAR(100) NOT NULL,
	acronym VARCHAR (15) NOT NULL,
	email VARCHAR(50) NOT NULL,
CONSTRAINT PK_universities PRIMARY KEY(name)
);

CREATE TABLE IF NOT EXISTS courses (
	id INTEGER AUTO_INCREMENT UNIQUE,
	university_id INTEGER NOT NULL,
	name VARCHAR(40) NOT NULL,
	acronym VARCHAR (15) NOT NULL,
CONSTRAINT PK_courses PRIMARY KEY (name),
CONSTRAINT FK_courses FOREIGN KEY (university_id) REFERENCES universities (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;

CREATE TABLE IF NOT EXISTS users (
	id INTEGER AUTO_INCREMENT UNIQUE,
	username VARCHAR(25) NOT NULL,
	password VARCHAR(40) NOT NULL,
	api_key VARCHAR(50) NOT NULL,
	email VARCHAR(60) NOT NULL UNIQUE,
	sex SET('m', 'f') NOT NULL,
	university INTEGER NOT NULL,
	course INTEGER NOT NULL,
	valid TINYINT(1) NOT NULL,
	hash VARCHAR(50) NOT NULL,
	last_seen DATETIME DEFAULT NULL,
	logged TINYINT(1) DEFAULT 0,
	special TINYINT(1) DEFAULT 0,
CONSTRAINT PK_users PRIMARY KEY (username),
CONSTRAINT FK_users_University FOREIGN KEY (university) REFERENCES universities (id),
CONSTRAINT FK_users_Cousers FOREIGN KEY (course) REFERENCES courses (id)
);

CREATE TABLE IF NOT EXISTS conversations (
	id INTEGER AUTO_INCREMENT, 
	user1 INTEGER NOT NULL,
	user2 INTEGER DEFAULT NULL,
	u1wantssex SET('m','f','w') NOT NULL,
	u1wantscourse INTEGER NULL,
	regId1 VARCHAR(200) NULL,
	regId2 VARCHAR(200) NULL,
	ready TINYINT(1) NOT NULL,
	participants INTEGER NOT NULL,
	started_on DATETIME NOT NULL,
	finished TINYINT(1) DEFAULT 0,
CONSTRAINT PK_conversations PRIMARY KEY (id),
CONSTRAINT FK_conversations_users_1 FOREIGN KEY (user1) REFERENCES users (id),
CONSTRAINT FK_conversations_users_2 FOREIGN KEY (user2) REFERENCES users (id),
CONSTRAINT FK_conversations_courses FOREIGN KEY (u1wantscourse) REFERENCES courses (id)
);

/* INSERTs */
/* universities and courses */
INSERT INTO universities (name, email, acronym) VALUES ('Universidade Federal de Viçosa', '@ufv.br', 'UFV');

INSERT INTO courses (university_id, name, acronym) VALUES (1, 'Administração', 'ADT');
INSERT INTO courses (university_id, name, acronym) VALUES (1, 'Agronomia', 'AGN');
INSERT INTO courses (university_id, name, acronym) VALUES (1, 'Arquitetura e Urbanismo', 'ARU');
INSERT INTO courses (university_id, name, acronym) VALUES (1, 'Bioquímica', 'BBQ');
INSERT INTO courses (university_id, name, acronym) VALUES (1, 'Ciência da Computação', 'CCP');
INSERT INTO courses (university_id, name, acronym) VALUES (1, 'Ciência e Tecnologia de Laticínios', 'TLA');
INSERT INTO courses (university_id, name, acronym) VALUES (1, 'Ciências Biológicas', 'BLG');
INSERT INTO courses (university_id, name, acronym) VALUES (1, 'Ciências Contábeis', 'CCO');
INSERT INTO courses (university_id, name, acronym) VALUES (1, 'Ciências Econômicas', 'CEC');
INSERT INTO courses (university_id, name, acronym) VALUES (1, 'Ciências Sociais', 'CSO');
INSERT INTO courses (university_id, name, acronym) VALUES (1, 'Comunicação Social', 'COM');
INSERT INTO courses (university_id, name, acronym) VALUES (1, 'Cooperativismo', 'GCO');
INSERT INTO courses (university_id, name, acronym) VALUES (1, 'Dança', 'DAN');
INSERT INTO courses (university_id, name, acronym) VALUES (1, 'Direito', 'DRT');
INSERT INTO courses (university_id, name, acronym) VALUES (1, 'Economia Doméstica', 'EDM');
INSERT INTO courses (university_id, name, acronym) VALUES (1, 'Educação Física', 'EFS');
INSERT INTO courses (university_id, name, acronym) VALUES (1, 'Educação Infantil', 'EIN');
INSERT INTO courses (university_id, name, acronym) VALUES (1, 'Enfermagem', 'EFG');
INSERT INTO courses (university_id, name, acronym) VALUES (1, 'Engenharia Agrícola e Ambiental', 'EAA');
INSERT INTO courses (university_id, name, acronym) VALUES (1, 'Engenharia Ambiental', 'EAB');
INSERT INTO courses (university_id, name, acronym) VALUES (1, 'Engenharia Civil', 'ECV');
INSERT INTO courses (university_id, name, acronym) VALUES (1, 'Engenharia de Agrimensura e Cartográfica', 'EAM');
INSERT INTO courses (university_id, name, acronym) VALUES (1, 'Engenharia de Alimentos', 'EAL');
INSERT INTO courses (university_id, name, acronym) VALUES (1, 'Engenharia de Produção', 'EPR');
INSERT INTO courses (university_id, name, acronym) VALUES (1, 'Engenharia Elétrica', 'EEL');
INSERT INTO courses (university_id, name, acronym) VALUES (1, 'Engenharia Florestal', 'EFL');
INSERT INTO courses (university_id, name, acronym) VALUES (1, 'Engenharia Mecânica', 'EGM');
INSERT INTO courses (university_id, name, acronym) VALUES (1, 'Engenharia Química', 'EGQ');
INSERT INTO courses (university_id, name, acronym) VALUES (1, 'Física', 'FCA');
INSERT INTO courses (university_id, name, acronym) VALUES (1, 'Geografia', 'GEO');
INSERT INTO courses (university_id, name, acronym) VALUES (1, 'Gestão do Agronegócio', 'GAG');
INSERT INTO courses (university_id, name, acronym) VALUES (1, 'História', 'HIS');
INSERT INTO courses (university_id, name, acronym) VALUES (1, 'Letras', 'LTR');
INSERT INTO courses (university_id, name, acronym) VALUES (1, 'Matemática', 'MTM');
INSERT INTO courses (university_id, name, acronym) VALUES (1, 'Medicina', 'MDC');
INSERT INTO courses (university_id, name, acronym) VALUES (1, 'Medicina Veterinária', 'MVT');
INSERT INTO courses (university_id, name, acronym) VALUES (1, 'Nutrição', 'NTR');
INSERT INTO courses (university_id, name, acronym) VALUES (1, 'Pedagogia', 'PED');
INSERT INTO courses (university_id, name, acronym) VALUES (1, 'Química', 'QCA');
INSERT INTO courses (university_id, name, acronym) VALUES (1, 'Secreteriado Executivo Trilingue', 'SEC');
INSERT INTO courses (university_id, name, acronym) VALUES (1, 'Zootecnia', 'ZOT');