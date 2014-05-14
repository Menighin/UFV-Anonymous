<?php

header("Content-Type: application/json; charset=utf-8");
	
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
		if (isset($_POST["message"])) {
			$message = json_encode(array("message" => $_POST["message"], "user_from" => $_POST['user']));
			
			// Get the GCM key
			try {
				$query = "SELECT * FROM users WHERE id = :user";
				$stmt = $conn->prepare($query);
				$stmt->execute(array(':user' => $_POST['user_to']));
				$row = $stmt->fetch();
			} catch (Exception $e) {
				echo json_encode(array('response' => -1));
				Log::writeLog("Erro na validaчуo em SEND_MESSAGE: " . $e->getMessage());
				$conn = $database->disconnect();
				exit(1);
			}
			
			$regId = $row['gcm_key'];
			
			include_once './GCM.php';
			 
			$gcm = new GCM();
		 
			$registration_ids = array($regId);
			$message = array("message" => $message);
		 
			$result = $gcm->send_notification($registration_ids, $message);
			
			// Update user last seen
			$conn->query("UPDATE users SET last_seen = NOW() WHERE id = '" . $_POST['user'] . "'");
			
			date_default_timezone_set('Brazil/East');
			echo json_encode(array('response' => 1, 'time' => date('H:i'), 'debug' => $_POST['message']));
		}
	}
	
	$conn = $database->disconnect();
?>