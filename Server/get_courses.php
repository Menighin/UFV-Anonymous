<?php
	
	/**
	* API get_courses function: return the list of the courses of the university the user belongs to
	* Author: João Menighin
	* Parameters:
	*	METHOD      => POST
	*	user        => int
	*   api_key     => string
	* Return: 
	*	 int response:
	*	   -1 ==> Database error
	*    	0 ==> If there's no messages to return
	*    	1 ==> If there's messages to return
	*             courses ==> Array of courses
	**/
	
	include "Database.class.php";
	include "Validate.class.php";
	include "Log.class.php";
	$database = new Database();
	$conn = $database->connect();
	$validate = new Validate($conn, $_POST['user'], $_POST['api_key']);
	
	// User not logged in
	if (!$validate->isValid()) {
		echo json_encode(array('response' => -2));
	}
	// User authenticated
	else {

		// Prepare query
		try {
			$stmt = $conn->prepare('SELECT * FROM courses WHERE university_id = (SELECT university FROM users WHERE id = :id) ORDER BY name');
			$stmt->execute(array(':id' => $_POST['user']));
			
			// Update user last seen
			$conn->query("UPDATE users SET last_seen = NOW() WHERE id = '" . $_POST['user'] . "'");
		} catch (Exception $e) {
			echo json_encode (array('response' => -1));
			Log::writeLog("Erro na validação em GET_COURSES: " . $e->getMessage());
			$conn = $database->disconnect();
			exit(1);
		}
		
		// Fetch
		$result = $stmt->fetchAll();
		
		if (!$result)
			echo json_encode(array('response' => 0));
		else
			echo json_encode(array('response' => 1, 'courses' => $result));
	}
	
	$conn = $database->disconnect();


?>