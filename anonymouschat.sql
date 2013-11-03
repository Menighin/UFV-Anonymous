-- phpMyAdmin SQL Dump
-- version 3.3.9
-- http://www.phpmyadmin.net
--
-- Servidor: localhost
-- Tempo de Geração: Nov 03, 2013 as 01:36 AM
-- Versão do Servidor: 5.5.8
-- Versão do PHP: 5.3.5

SET SQL_MODE="NO_AUTO_VALUE_ON_ZERO";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;

--
-- Banco de Dados: `anonymouschat`
--
CREATE DATABASE `anonymouschat` DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci;
USE `anonymouschat`;

-- --------------------------------------------------------

--
-- Estrutura da tabela `conversations`
--

CREATE TABLE IF NOT EXISTS `conversations` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `user1` int(11) unsigned NOT NULL,
  `user2` int(11) unsigned DEFAULT NULL,
  `u1sex` set('m','f','w') NOT NULL,
  `u1course` int(11) unsigned NOT NULL,
  `u1university` int(11) unsigned NOT NULL,
  `u1wantssex` set('m','f','w') NOT NULL,
  `u1wantscourse` int(11) unsigned NOT NULL,
  `ready` tinyint(1) NOT NULL,
  `participants` int(11) NOT NULL,
  `started_on` datetime NOT NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `user1_fk` FOREIGN KEY (`user1`) REFERENCES `users`(`id`),
  CONSTRAINT `user2_fk` FOREIGN KEY (`user2`) REFERENCES `users`(`id`),
  CONSTRAINT `u1course_fk` FOREIGN KEY (`u1course`) REFERENCES `courses`(`id`),
  CONSTRAINT `u1university_fk` FOREIGN KEY (`u1university`) REFERENCES `universities`(`id`),
  CONSTRAINT `u1wcourse_fk` FOREIGN KEY (`u1wantscourse`) REFERENCES `courses`(`id)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=64 ;

--
-- Extraindo dados da tabela `conversations`
--


-- --------------------------------------------------------

--
-- Estrutura da tabela `courses`
--

CREATE TABLE IF NOT EXISTS `courses` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `university_id` int(11) unsigned NOT NULL,
  `name` varchar(40) NOT NULL,
  PRIMARY KEY (`id`,`name`),
  FOREIGN KEY (university_id) REFERENCES universities(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;

--
-- Extraindo dados da tabela `courses`
--


-- --------------------------------------------------------

--
-- Estrutura da tabela `messages`
--

CREATE TABLE IF NOT EXISTS `messages` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `conversation_id` int(11) unsigned NOT NULL,
  `message` varchar(500) NOT NULL,
  `time` datetime NOT NULL,
  `author` tinyint(4) NOT NULL,
  `is_read` tinyint(1) NOT NULL,
  `END_FLAG` tinyint(4) NOT NULL,
  PRIMARY KEY (`id`),
  FOREIGN KEY (conversation_id) REFERENCES conversations(id)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=183 ;

--
-- Extraindo dados da tabela `messages`
--


-- --------------------------------------------------------

--
-- Estrutura da tabela `universities`
--

CREATE TABLE IF NOT EXISTS `universities` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(100) NOT NULL,
  `email` varchar(50) NOT NULL,
  PRIMARY KEY (`id`,`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;

--
-- Extraindo dados da tabela `universities`
--


-- --------------------------------------------------------

--
-- Estrutura da tabela `users`
--

CREATE TABLE IF NOT EXISTS `users` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `username` varchar(25) NOT NULL,
  `password` varchar(15) NOT NULL,
  `api_key` varchar(50) NOT NULL,
  `email` varchar(60) NOT NULL,
  `university` int(11) unsigned NOT NULL,
  `sex` set('m','f') NOT NULL,
  `course` int(11) unsigned NOT NULL,
  `valid` tinyint(1) NOT NULL,
  `hash` varchar(50) NOT NULL,
  `last_seen` datetime DEFAULT NULL,
  PRIMARY KEY (`id`,`username`,`email`),
  FOREIGN KEY (university) REFERENCES universities(id) ,
  FOREIGN KEY (course) REFERENCES courses(id) 
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=3 ;

--
-- Extraindo dados da tabela `users`
--

INSERT INTO `users` (`id`, `username`, `password`, `api_key`, `email`, `university`, `sex`, `course`, `valid`, `hash`, `last_seen`) VALUES
(1, 'menighin', '12345', 'TesteNaVerdadeVaiSerUmRandomHexadecimal', 'joao.menighin@ufv.br', 0, 'm', 0, 1, '', '2013-11-02 01:28:40'),
(2, 'thiago', '12345', 'TesteNaVerdadeVaiSerUmRandomHexadecimal2', 'thiago@gmail.com', 0, 'm', 0, 1, '', NULL);
