<?php

	/**
	* API login function
	* Author: Jo�o Menighin
	* Parameters:
	*	METHOD   => POST
	*	user ==> int
	* Return: int:
	*   -1 ==> Error ocurred
	*    1 ==> User logout
	**/

	session_start(); 
	include 'Database.class.php';
	$database = new Database();
	$validate = new Validate($conn, $_POST['user'], $_POST['api_key']);
	$conn = $database->connect();
	
	if (!$validate->isValid()) {
		echo json_encode(array('response' => -2));
	}
	// User authenticated
	else {
		try {
			$conn->query("UPDATE users SET logged = 1 WHERE id = " . $_POST['user']);
			echo json_encode(array("response" => 1));
		} catch (Exception $e) {
			echo json_encode(array("response" => -1));
		}
	}
	
	$conn = $database->disconnect();

?>