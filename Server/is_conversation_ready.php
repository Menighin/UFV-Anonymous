<?php
	
	/**
	* API function to constantly check if another user connected into a created conversation
	* Author: João Menighin
	* Parameters:
	*	METHOD      => POST
	*	user        => int
	*   api_key     => string
	* Return: 
	*	 int response:
	*	   -2 ==> Invalid API Key for user
	* 	   -1 ==> If some error ocurred in database queries
	*		0 ==> If ready == 0, thus another user didn't connect yet
	*    	1 ==> If ready == 1, thus another user connected
	*
	**/
	
	include "Database.class.php";
	include "Validate.class.php";
	$database = new Database();
	$conn = $database->connect();
	$validate = new Validate($conn, $_POST['user'], $_POST['api_key']);
	
	if (isset($_POST['conversation_id']) && isset($_POST['user']) && isset($_POST['api_key'])) {
		$validate = new Validate($conn, $_POST['user'], $_POST['api_key']);
		// Invalid API Key
		if (!$validate->isValid()) {
			try {
				$stmt = $conn->prepare("UPDATE conversations SET ready = 1 WHERE id = :id");
				$stmt->execute(array(':id' => $_POST['conversation_id']));
			} catch (Exception $e) {
				echo json_encode(array('response' => -1));
				$conn = $database->disconnect();
				exit(1);
			}
			echo json_encode(array('response' => -2));
		}
		// User authenticated
		else {
			// Prepare query
			try {
				$query = 'SELECT C.ready, U2.username AS username, U2.special AS special
							FROM conversations C
							INNER JOIN users U2
							ON C.user2 = U2.id
							WHERE C.id=:id';
				$stmt = $conn->prepare($query);
				$stmt->execute(array(':id' => $_POST['conversation_id']));
			} catch (Exception $e) {
				echo $e->getMessage();
				echo json_encode(array('response' => -1));
				$conn = $database->disconnect();
				exit(1);
			}
			
			// Execute it
			$row = $stmt->fetch();
			
			if (!$row)
				echo json_encode(array('response' => 0));
			else
				echo json_encode(array('response' => 1, 'username' => $row['username'], 'special' => $row['special']));
		}
	}
	
	$conn = $database->disconnect();

?>