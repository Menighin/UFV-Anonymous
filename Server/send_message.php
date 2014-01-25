<?php
	
	/**
	* API send_message function used to send message to database so other user can fetch it. It closes the conversation (ready = 1) wether any of them send an END_FLAG message.
	* Author: João Menighin
	* Parameters:
	*	METHOD          => POST
	*	conversation_id => int
	*   message         => string
	*   author          => int
	*   flag            => int
	*	user			=> int
	*   api_key         => string
	* Return: 
	*	 int response:
	*	   -2 ==> Invalid API Key for user
	* 	   -1 ==> If some error ocurred
	*    	1 ==> If message was sent successfully
	**/
	
	header("Content-Type: application/json; charset=utf-8");
	
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

		// Prepare and execute query
		try {
			// End conversation withouth 2 users anyway
			if ($_POST['flag'] == "1") {
				try {
					$stmt = $conn->prepare("UPDATE conversations SET ready = 1 WHERE id = :id");
					$stmt->execute(array(':id' => $_POST['conversation_id']));
				} catch (Exception $e) {
					echo json_encode(array('response' => -1));
					$conn = $database->disconnect();
					exit(1);
				}
			}
			$stmt = $conn->prepare("INSERT INTO messages (conversation_id, message, time, author, is_read, END_FLAG) VALUES (:id, :msg, NOW(), :author, 0, :flag)");
			$stmt->execute(array(':id' => $_POST['conversation_id'], ':msg' => $_POST['message'], ':author' => $_POST['author'], ':flag' => $_POST['flag']));
			$conn->query("UPDATE users SET last_seen = NOW() WHERE id = '" . $_POST['user'] . "'");
		} catch (Exception $e) {
			echo json_encode(array('response' => -1));
			$conn = $database->disconnect();
			exit(1);
		}
		
		date_default_timezone_set('Brazil/East');
		echo json_encode(array('response' => 1, 'time' => date('H:i'), 'debug' => $_POST['message']));
	}
	
	$conn = $database->disconnect();

?>