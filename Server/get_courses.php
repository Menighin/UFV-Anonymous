<?php

	include "Database.class.php";
	include "Validate.class.php";
	$database = new Database();
	$conn = $database->connect();
	$validate = new Validate($conn, $_POST['user'], $_POST['api_key']);
	
	// User not logged in
	if (!$validate->isValid()) {
		echo json_encode(array('response' => -1));
	}
	// User authenticated
	else {

		// Prepare query
		$stmt = $conn->prepare('SELECT * FROM courses WHERE university_id = (SELECT university FROM users WHERE id = :id) ORDER BY name');
		$stmt->execute(array(':id' => $_POST['user']));
		
		// Fetch
		$result= $stmt->fetchAll();
		
		if (!$result)
			echo json_encode(array('response' => 0));
		else
			echo json_encode(array('response' => 1, 'courses' => $result));
	}
	
	$conn = $database->disconnect();


?>