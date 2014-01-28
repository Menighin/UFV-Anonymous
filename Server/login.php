<?php

	/**
	* API login function
	* Author: Jo�o Menighin
	* Parameters:
	*	METHOD   => POST
	*	username => string
	*	password => string
	* Return: int:
	*	-4 ==> Incorrect password or user
	*	-2 ==> User already logged
	* 	-1 ==> Database error
	*    0 ==> Not validated user
	*    1 ==> User correct (do the login)
	**/
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
		echo json_encode(array('response' => -4));
	else {
		if (strcmp($row['password'], md5(strtolower($_POST['password']))) == 0) {
			if ($row['valid'] == true) {
				if ($row['logged'] == 0) {
					try {
						$key = md5(uniqid(rand(), true));
						$conn->query("UPDATE users SET last_seen = NOW(), logged = 1, api_key = '". $key ."' WHERE username = '" . $_POST['username'] . "'");
						echo json_encode(array('response' => 1, 'id' => $row['id'], 'username' => $row['username'], 'courseID' => $row['course'], 'sex' => $row['sex'], 'universityID' => $row['university'], 'apikey' => $key));
					} catch (Exception $e) {
						echo $e->getMessage();
						echo json_encode(array('response' => -1));
					}
				} else { // User logged in other device
					// It changes de API Key so it allows the user to log in another device
					try {
						$key = md5(uniqid(rand(), true));
						$conn->query("UPDATE users SET last_seen = NOW(), logged = 1, api_key = '". $key ."' WHERE username = '" . $_POST['username'] . "'");
						echo json_encode(array('response' => -2, 'id' => $row['id'], 'username' => $row['username'], 'courseID' => $row['course'], 'sex' => $row['sex'], 'universityID' => $row['university'], 'apikey' => $key));
					} catch (Exception $e) {
						echo $e->getMessage();
						echo json_encode(array('response' => -1));
					}
				}
			} else {
				echo json_encode(array('response' => 0));
			}
		} else {
			echo json_encode(array('response' => -4));
		}
	}
    
	$conn = $database->disconnect();
?>