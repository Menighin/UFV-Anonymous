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
	*    	0 ==> If the user created a new converstion (i.e. server function)
	*    	1 ==> If the user connected to someone (i.e. client function)
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
	
		// Close other conversations the user may be into
		$query = "SELECT id, user1, user2, regId1, regId2 FROM conversations 
			WHERE ready = 1 AND finished = 0 AND (user1 = '" . $_POST['user'] . "' OR user2 = '" . $_POST['user'] . "')";
		
		try {
			$stmt = $conn->prepare($query);
			$stmt->execute();
		} catch (Exception $e) {
			echo $e->getMessage();
			echo json_encode(array('response' => -1));
			$conn = $database->disconnect();
			exit(1);
		}
		
		$result = $stmt->fetchAll();
		
		if (count($result) > 0) {
			include_once './GCM.php';
			$gcm = new GCM();
			$registration_ids = array();
			
			foreach ($result as $row) {
				$regId = ($row['user1'] == $_POST['user']) ? $row['regId2'] : $row['regId1'];
				array_push($registration_ids, $regId);
				$conn->query("UPDATE conversations SET finished = 1 WHERE id = '" . $row['id'] . "'");
			}
			
			$message = array("message" => "[fechaOChatUniChat]");
		 
			$result = $gcm->send_notification($registration_ids, $message);
		}
		
		// Prepare query to select avaiable conversations
		if ($_POST['wantscourse'] == -1)
			$wantscourse = null;
		else
			$wantscourse = $_POST['wantscourse'];
		
		$query = "SELECT C.id, 
					U1.id AS u1id, U1.username AS user1, U1.sex AS u1sex, U1.course AS u1course, U1.university AS u1university, U1.special AS u1special,
					C.u1wantssex, C.u1wantscourse, C.ready, C.regId1, C.participants
					FROM conversations C
					INNER JOIN users U1
					ON C.user1 = U1.id
					WHERE ready = 0 AND 
					C.user1 != :user AND
					U1.university = (SELECT university FROM users Where id = :user) AND
					(C.u1wantssex = 'w' OR C.u1wantssex = (SELECT sex FROM users WHERE id = :user)) AND 
					(C.u1wantscourse IS NULL OR C.u1wantscourse = (SELECT course FROM users WHERE id = :user))";
		
		$params = array(':user' => $_POST['user']);
		if ($_POST['wantssex'] != 'w') {
			$query .= " AND U1.sex = :wantssex";
			$params[':wantssex'] = $_POST['wantssex'];
		}
		if ($wantscourse != null) {
			$query .= " AND U1.course = :wantscourse";
			$params[':wantscourse'] = $wantscourse;
		}
		
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
		 
		// Check if there is open conversations
		if (count($result) <= 0) {
			// Open new conversation - You will be user 1
			$query = 'INSERT INTO conversations (
						user1, u1wantssex, u1wantscourse, regId1, ready, participants, started_on)
						VALUES (:user, :wantssex, :wantscourse, :regId1, 0, 1, NOW())';
						
			try {
				$stmt = $conn->prepare($query);
				$stmt->execute(array (':user' => $_POST['user'], ':wantssex' => $_POST['wantssex'], ':wantscourse' => $wantscourse, ':regId1' => $_POST['regId']));
				echo json_encode (array('response' => 0, 'conversation_id' => $conn->lastInsertId()));
			} catch (Exception $e) {
				//echo $e->getMessage();
				echo json_encode(array('response' => -1));
			}
		} else {
			// Select random conversation to talk with - You will be user 2
			
			try {
				$conn->query("UPDATE conversations SET user2 = '" . $_POST['user'] . "', ready = 1, participants = 2, regId2 = '" . $_POST['regId'] . 
					"' WHERE id = " . $result[$random = mt_rand(0, count($result) - 1)]['id'] . " AND ready = 0");
				echo json_encode (array('response' => 1, 'conversation_id' => $result[$random]['id'], 'username' => $result[$random]['user1'], 'special' => $result[$random]['u1special'],
					'regId' => $result[$random]['regId1'], 'userId' => $result[$random]['u1id']));
				
				// Send message to user 1 telling him the conversation has started
				$query = 'SELECT * FROM users WHERE id = :user';
				$stmt = $conn->prepare($query);
				$stmt->execute(array(':user' => $_POST['user']));
				$row = $stmt->fetch();
				
				$userAlias = "Anônimo";
				if ($row['special'] == 1)
					$userAlias = $row['username'];
				
				$json = array("message" => "[abreOChatUniChat]", "user" => $userAlias, "user_id" => $_POST['user'], "regId" => $_POST['regId']);
				
				include_once './GCM.php';
			 
				$gcm = new GCM();
				
				$registration_ids = array($result[$random]['regId1']);
			 
				$gcm->send_notification($registration_ids, $json);
				
			} catch (Exception $e) {
				//echo $e->getMessage();
				echo json_encode(array('response' => -1));
			}
		}
	}
	
	$conn = $database->disconnect();
	
?>