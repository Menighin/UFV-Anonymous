<?php

	/**
	* API connection function: Decide weather the user is going to create a new conversation or connect to an existent one
	* Author: João Menighin
	* Parameters:
	*	METHOD      => POST
	*	user        => string
	*	sex         => string
	*   course      => string
	*   wantssex    => string
	*   wantscourse => string
	* Return: 
	*	 int response:
	* 	   -1 ==> If the user is not using a valid key
	*    	0 ==> If the user created a new converstion (i.e. server function)
	*    	1 ==> If the user connected to someone (i.e. client function)
	*	 int conversation_id:
	*		If the response is >= 0, return the id of the conversation that was created / connected.
	**/
	
	header("Content-Type: text/html;charset=utf-8");
	
	// User not logged in
	/*if (!isset($_SESSION['user'])) {
		echo json_encode(array('response' => -1));
	}*/
	// User authenticated
	//else {
		include "database.class.php";
		$database = new Database();
		$conn = $database->connect();
		
		// Prepare query
		$stmt = $conn->prepare('SELECT * FROM conversations WHERE ready=0');
		$stmt->execute();
		
		// Execute it
		$result = $stmt->fetchAll();
		 
		// Check if there is open conversations
		if (count($result) <= 0) {
			// Open new conversation
			$query = 'INSERT INTO conversations (
						user1, u1sex, u1course, u1wantssex, u1wantscourse, ready, participants, started_on)
						VALUES (:user, :sex, :course, :wantssex, :wantscourse, 0, 1, NOW())';
						
			$stmt = $conn->prepare($query);
			$stmt->execute(array (':user' => $_POST['user'], ':sex' => $_POST['sex'], ':course' => $_POST['course'], ':wantssex' => $_POST['wantssex'], ':wantscourse' => $_POST['wantscourse']));
			
			echo json_encode (array('response' => 0, 'conversation_id' => $conn->lastInsertId()));
		} else {
			// Select random conversation to talk with
			
			$conn->query("UPDATE conversations SET user2 = '" . $_POST['user'] . "', ready = 1, participants = 2 WHERE id = " . $result[$random = mt_rand(0, count($result) - 1)]['id'] . " AND ready = 0");
			
			echo json_encode (array('response' => 1, 'conversation_id' => $result[$random]['id']));
		}
	//}
	
?>