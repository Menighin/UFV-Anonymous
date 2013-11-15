<?php
	
	/**
	* API get_message function: return the message to the user if there is a message to return
	* Author: Jo�o Menighin
	* Parameters:
	*	METHOD          => POST
	*	conversation_id => int
	*   author          => int
	*	user			=> int
	*   api_key         => string
	* Return: 
	*	 int response:
	*	   -2 ==> Invalid API Key for user
	*	   -1 ==> Database error
	*    	0 ==> If there's no messages to return
	*    	1 ==> If there's messages to return
	*             messages ==> Array of messages
	**/

	header("Content-Type: text/html; charset=utf-8");

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
		try {
			$stmt = $conn->prepare('SELECT * FROM messages WHERE conversation_id=:id AND is_read = 0 AND author = :author');
			$stmt->execute(array(':id' => $_POST['conversation_id'], ':author' => $_POST['author']));
		} catch (Exception $e) {
			echo json_encode (array('response' => -1));
			$conn = $database->disconnect();
			exit(1);
		}
		
		// Fetch
		$result= $stmt->fetchAll();
		
		foreach ($result as $row)
			$conn->query("UPDATE messages SET is_read = 1 WHERE id = '" . $row['id'] . "'");
		
		if (!$result)
			echo json_encode(array('response' => 0));
		else
			echo json_encode(array('response' => 1, 'messages' => $result));
	}
	
	$conn = $database->disconnect();

?>