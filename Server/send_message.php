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
		if (isset($_POST["regId"]) && isset($_POST["message"])) {
			$regId = $_POST["regId"];
			$message = $_POST["message"];
			 
			include_once './GCM.php';
			 
			$gcm = new GCM();
		 
			$registatoin_ids = array($regId);
			$message = array("message" => $message);
		 
			$result = $gcm->send_notification($registatoin_ids, $message);
			
			// Update user last seen
			$conn->query("UPDATE users SET last_seen = NOW() WHERE id = '" . $_POST['user'] . "'");
			
			// Closing conversation whenever one leaves
			if (strcmp($_POST['message'], "[fechaOChatUniChat]") == 0)
				try {
					$stmt = $conn->prepare("UPDATE conversations SET ready = 1 WHERE id = :id");
					$stmt->execute(array(':id' => $_POST['conversation_id']));
				} catch (Exception $e) {
					echo json_encode(array('response' => -1));
					Log::writeLog("Erro na validação em SEND_MESSAGE: " . $e->getMessage());
					$conn = $database->disconnect();
					exit(1);
				}
			
			date_default_timezone_set('Brazil/East');
			echo json_encode(array('response' => 1, 'time' => date('H:i'), 'debug' => $_POST['message']));
		}
	}
?>