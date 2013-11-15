<?php

	/**
	* API login function
	* Author: João Menighin
	* Parameters:
	*	METHOD   => POST
	*	username => string
	*	password => string
	* Return: int:
	*	-3 ==> Incorrect password
	*	-2 ==> User already logged
	* 	-1 ==> Database error
	*    0 ==> Not validated user
	*    1 ==> User correct (do the login)
	**/

	session_start(); 
	include 'Database.class.php';
	$database = new Database();
	$conn = $database->connect();
	
	try {
		$stmt = $conn->prepare('SELECT * FROM users WHERE username=:username');
		$stmt->execute(array('username' => $_POST['username']));
	} catch (Exception $e) {
		echo json_encode(array('response' => -1));
		$conn = $database->disconnect();
		exit(1);
	}
	
    $row = $stmt->fetch();
	if (!$row)
		echo json_encode(array('response' => -1));
	else {
		if (strcmp($row['password'], $_POST['password']) == 0) {
			if ($row['valid'] == true) {
				if ($row['logged'] == 0) {
					try {
						$conn->query("UPDATE users SET last_seen = NOW(), logged = 1 WHERE username = '" . $_POST['username'] . "'");
						echo json_encode(array('response' => 1, 'id' => $row['id'], 'username' => $row['username'], 'courseID' => $row['course'], 'sex' => $row['sex'], 'universityID' => $row['university'], 'apikey' => $row['api_key']));
					} catch (Exception $e) {
						echo json_encode(array('response' => -1));
					}
				} else {
					echo json_encode(array('response' => -2));
				}
			} else {
				echo json_encode(array('response' => 0));
			}
		} else {
			echo json_encode(array('response' => -3));
		}
	}
    
	$conn = $database->disconnect();
?>