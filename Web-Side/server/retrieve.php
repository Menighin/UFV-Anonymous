<?php

	include 'Database.class.php';
	$database = new Database();
	
	if (!isset($_POST['attr']) || !isset($_POST['param']))
		throw new Exception ("Missing parameters");
	
	$conn = $database->connect();
	
	$param = explode (";", $_POST['param']);
	$attr = $_POST['attr'];
	
	
	try {
				$stmt = $this->conn->prepare('SELECT COUNT(*) FROM users WHERE ' . $this->attr . '=:attr');
				$stmt->execute(array('attr' => $this->param);
			} catch (Exception $e) {
				$this->conn = null;
				exit(1);
			}
			
			$row = $stmt->fetch();
	