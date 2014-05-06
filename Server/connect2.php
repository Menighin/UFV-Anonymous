<?php

	/**
	* API connection function: Decide weather the user is going to create a new conversation or connect to an existent one
	* Author: João Menighin
	* Parameters:
	*	METHOD      => POST
	*	user        => string
	*   api_key     => string
	*	sex         => string
	*   course      => string
	*   wantssex    => string
	*   wantscourse => string
	* Return: 
	*	 int response:
	*	   -2 ==> Invalid API Key for user
	* 	   -1 ==> If the user is not using a valid key
	*    	0 ==> If there is no one that fits the criteria
	*    	1 ==> If the user connected to someone
	*	 int conversation_id:
	*		If the response is >= 0, return the id of the conversation that was created / connected.
	**/
	session_start();
	header("Content-Type: text/html;charset=utf-8");
	
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
		
		// Update user last seen
		$conn->query("UPDATE users SET last_seen = NOW() WHERE id = '" . $_POST['user'] . "'");
		
		// Prepare query to select avaiable users
		if ($_POST['wantscourse'] == -1)
			$wantscourse = null;
		else
			$wantscourse = $_POST['wantscourse'];
		
		$query = "SELECT *
					FROM users
					WHERE university = (SELECT university FROM users WHERE id = :user)
						AND id != :user";
					
		$params = array(':user' => $_POST['user']);
		if ($_POST['wantssex'] != 'w') {
			$query .= " AND sex = :wantssex";
			$params[':wantssex'] = $_POST['wantssex'];
		}
		if ($wantscourse != null) {
			$query .= " AND course = :wantscourse";
			$params[':wantscourse'] = $wantscourse;
		}
		
		$query .= ' AND gcm_key IS NOT NULL AND gcm_key != "" ORDER BY last_seen DESC LIMIT 30';
		
		// Execute query!
		try {
			$stmt = $conn->prepare($query);
			$stmt->execute($params);
		} catch (Exception $e) {
			echo $e->getMessage();
			echo json_encode(array('response' => -1));
			$conn = $database->disconnect();
			exit(1);
		}
		
		// Fetch results
		$result = $stmt->fetchAll();
		 
		// Check if there is results for the specific criteria
		if (count($result) <= 0) {			
			echo json_encode (array('response' => 0));
		} else {
			// Select random stranger among the 20 to talk with
			// And the winner is...
			$random = mt_rand(0, count($result - 1));
			
			echo json_encode (array('response' => 1, 'user_id' => $result[$random]['id'], 'username' => $result[$random]['username'], 'special' => $result[$random]['special']));
		}
	}
	
	$conn = $database->disconnect();
	
?>