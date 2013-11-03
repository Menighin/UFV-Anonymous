<?php
 class Database {
 
	private $conn;
	private $username = "root", $password = "", $host = "localhost", $dbname = "anonymouschat";
	
	function connect() {
		try {
			$this->conn = new PDO('mysql:host='.$this->host.';dbname='.$this->dbname, $this->username, $this->password);
			$this->conn->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
			$this->conn->exec('SET CHARACTER SET utf8');
			$this->conn->exec('SET NAMES utf8');
		} catch(PDOException $e) {
			echo 'ERROR: ' . $e->getMessage();
		}

		return $this->conn;
	}
	
	function disconnect() {
		$this->conn = null;
		return $this->conn;
	}
}
	
?>