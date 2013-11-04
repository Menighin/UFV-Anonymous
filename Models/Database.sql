CREATE DATABASE `anonymouschat` DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci;
USE `anonymouschat`;

CREATE TABLE IF NOT EXISTS universities (
	id INTEGER AUTO_INCREMENT,
	name VARCHAR(100) NOT NULL,
	email VARCHAR(50) NOT NULL,
CONSTRAINT PK_Universities PRIMARY KEY(id, name)
);

CREATE TABLE IF NOT EXISTS courses (
	id INTEGER AUTO_INCREMENT,
	university_id INTEGER NOT NULL,
	name VARCHAR(40) NOT NULL,
CONSTRAINT PK_Courses PRIMARY KEY (id, name),
CONSTRAINT FK_Courses FOREIGN KEY (university_id) REFERENCES universities (id)
);

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
CONSTRAINT PK_Users PRIMARY KEY (id, username, email),
CONSTRAINT FK_Users_University FOREIGN KEY (university) REFERENCES universities (id),
CONSTRAINT FK_Users_Cousers FOREIGN KEY (course) REFERENCES courses (id)
);

CREATE TABLE IF NOT EXISTS conversations (
	id INTEGER AUTO_INCREMENT, 
	user1 INTEGER NOT NULL,
	user2 INTEGER NOT NULL,
	u1wantssex SET('m','f','w') NOT NULL,
	u1wantscourse INTEGER NOT NULL,
	ready TINYINT(1) NOT NULL,
	participants INTEGER NOT NULL,
	started_on DATETIME NOT NULL,
CONSTRAINT PK_Conversations PRIMARY KEY (id),
CONSTRAINT FK_Conversations_Users_1 FOREIGN KEY (user1) REFERENCES users (id),
CONSTRAINT FK_Conversations_Users_2 FOREIGN KEY (user2) REFERENCES users (id),
CONSTRAINT FK_Conversations_Courses FOREIGN KEY (u1wantscourse) REFERENCES courses (id)
);

CREATE TABLE IF NOT EXISTS messages (
	id INTEGER AUTO_INCREMENT,
	conversation_id INTEGER NOT NULL,
	message TEXT NOT NULL,
	time DATETIME NOT NULL,
	author INTEGER NOT NULL,
	is_read TINYINT(1) NOT NULL,
	END_FLAG TINYINT(1) NOT NULL,
CONSTRAINT PK_Messages PRIMARY KEY (id),
CONSTRAINT FK_Messages_Conversation FOREIGN KEY (conversation_id) REFERENCES conversations (id),
CONSTRAINT FK_Messages_Author FOREIGN KEY (author) REFERENCES users (id)
);