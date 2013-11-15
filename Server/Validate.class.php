<?php
	
	/**
	* Class used to check if the API Key is valid for the user
	* Author: João Menighin
	**/
	class Validate {
		
		private $conn;
		private $userid;
		private $api_key;
		
		/**
		* Class constructor
		* Parameters:
		*	$conn    ==> PDO Connection with the database
		*	$userid  ==> ID of the user in the database
		*	$api_key ==> Supposedly the API key for the given user
		**/
		function __construct ($conn, $userid, $api_key) {
			$this->conn = $conn;
			$this->userid = $userid;
			$this->api_key = $api_key;
		}
		
		/**
		* Function to check if the API Key is valid
		* Parameters: None
		* Return:
		*	bool: Wether the API Key is valid or not for the given user
		**/
		public function isValid() {
			$stmt = $this->conn->prepare('SELECT * FROM users WHERE id=:userid');
			$stmt->execute(array('userid' => $this->userid));
			
			$row = $stmt->fetch();
			if (!$row)
				return false;
			else {
				if (strcmp($row['api_key'], $this->api_key) == 0 && $row['valid'] == true)
					return true;
				else
					return false;
			}
		}
		
		/** 
		* Destructor to destroy the connection
		**/
		function __destruct() {
			$this->conn = null;
		}
	}


?>