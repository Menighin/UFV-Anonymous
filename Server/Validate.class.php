<?php

	class Validate {
		
		private $conn;
		private $userid;
		private $api_key;
		
		function __construct ($conn, $userid, $api_key) {
			$this->conn = $conn;
			$this->userid = $userid;
			$this->api_key = $api_key;
		}
		
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
		
		function __destruct() {
			$this->conn = null;
		}
	}


?>