<?php

	include "Database.class.php";
	include "Validate.class.php";
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
		$stmt = $conn->prepare('SELECT ready FROM conversations WHERE id=:id');
		$stmt->execute(array(':id' => $_POST['conversation_id']));
		
		// Execute it
		$row = $stmt->fetch();
		
		if (!$row)
			echo json_encode(array('response' => -1));
		else
			echo json_encode(array('response' => (int)$row[0]));
	}
	
	$conn = $database->disconnect();

?>