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
			$message = array("price" => $message);
		 
			$result = $gcm->send_notification($registatoin_ids, $message);
		 
			echo $result;
		}
	}
?>