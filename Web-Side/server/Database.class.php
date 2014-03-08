<?php

	/**
	* Class used to create the database connection
	* Author: João Menighin
	**/
	class Database {
	 
		private $conn;
		//private $username = "root", $password = "", $host = "localhost", $dbname = "unichat";
		private $username = "root", $password = "WyDadaVaX", $host = "93.188.161.199", $dbname = "unichat";
		
		/**
		* Funcion to create a connection with the database info
		* Return:
		*	Connection with database if sucessfull, null otherwise
		**/
		public function connect() {
			try {
				$this->conn = new PDO('mysql:host='.$this->host.';port=9876;dbname='.$this->dbname, $this->username, $this->password);
				$this->conn->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
				$this->conn->exec('SET CHARACTER SET utf8');
				$this->conn->exec('SET NAMES utf8');
			} catch(PDOException $e) {
				echo 'ERROR: ' . $e->getMessage();
				return null;
			}

			return $this->conn;
		}
		
		/**
		* Destroy the connection
		**/
		public function disconnect() {
			$this->conn = null;
			return $this->conn;
		}
	}
	
?>