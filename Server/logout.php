<?php

	/**
	* API logout function
	* Author: João Menighin
	* Parameters:
	*	METHOD   => POST
	*	user ==> int
	* Return: int:
	*   -2 ==> Invalid API Key for user
	*   -1 ==> Database error
	*    1 ==> User logout
	**/

	session_start(); 
	include 'Database.class.php';
	include "Validate.class.php";
	$database = new Database();
	$conn = $database->connect();
	$validate = new Validate($conn, $_POST['user'], $_POST['api_key']);
	
	
	if (!$validate->isValid()) {
		echo json_encode(array('response' => -2));
	}
	// User authenticated
	else {
		try {
			$conn->query("UPDATE users SET logged = 0 WHERE id = " . $_POST['user']);
			echo json_encode(array("response" => 1));
		} catch (Exception $e) {
			echo json_encode(array("response" => -1));
		}
	}
	
	$conn = $database->disconnect();

?>