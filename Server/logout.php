<?php

	/**
	* API logout function
	* Author: João Menighin
	* Parameters:
	*	METHOD   => POST
	*	user     => int
	*   api_key  => string
	* Return: int:
	*   -2 ==> Invalid API Key for user
	*   -1 ==> Database error
	*    1 ==> User logout
	**/

	session_start(); 
	include 'Database.class.php';
	include "Validate.class.php";
	include "Log.class.php";
	$database = new Database();
	$conn = $database->connect();
	$validate = new Validate($conn, $_POST['user'], $_POST['api_key']);
	
	
	if (!$validate->isValid()) {
		echo json_encode(array('response' => -2));
	}
	// User authenticated
	else {
		try {
			$key = md5(uniqid(rand(), true));
			$conn->query("UPDATE users SET logged = 0, api_key = '" . $key . "' WHERE id = " . $_POST['user']);
			Log::writeLog($_POST['username'] . " fez LOGOUT");
			echo json_encode(array("response" => 1));
		} catch (Exception $e) {
			Log::writeLog("Erro em LOGOUT: " . $e->getMessage());
			echo json_encode(array("response" => -1));
		}
	}
	
	$conn = $database->disconnect();

?>