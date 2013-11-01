<?php

	/**
	* API login function
	* Author: João Menighin
	* Parameters:
	*	METHOD   => POST
	*	username => string
	*	password => string
	* Return: int:
	* 	-1 ==> User inexistent / incorrect password
	*    0 ==> Not validated user
	*    1 ==> User correct (do the login)
	**/

	session_start(); 
	include 'database.class.php';
	$database = new Database();
	$conn = $database->connect();
	
	$stmt = $conn->prepare('SELECT * FROM users WHERE username=:username');
    $stmt->execute(array('username' => $_POST['username']));
	
	
    $row = $stmt->fetch();
	if (!$row)
		echo json_encode(array('response' => -1));
	else {
		if (strcmp($row['password'], $_POST['password']) == 0)
			if ($row['valid'] == true) {
				echo json_encode(array('response' => 1, 'username' => $row['username'], 'course' => $row['course'], 'sex' => $row['sex']));
				$_SESSION['user'] = $_POST['username'];
				
				$conn->query("UPDATE users SET last_seen = NOW() WHERE username = '" . $_POST['username'] . "'");
				
			}
			else
				echo json_encode(array('response' => 0));
		else
			echo json_encode(array('response' => -1));
	}
    
	$conn = $database->disconnect();
?>