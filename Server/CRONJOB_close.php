<?php

	header("Content-Type: application/json; charset=utf-8");
	
	define('TEMPO', 177);
	
	include "Database.class.php";
	include "Validate.class.php";
	$database = new Database();
	$conn = $database->connect();
	
	class Log {
		public static function writeLog($message) {
			
			$timestamp = "[" . date("H:i:s") . "] ";
			file_put_contents ("logs/" . date("Y-m-d"). ".txt", $timestamp . $message . "\n", FILE_APPEND);
			
		}
	}
	
	// Selecting all running conversations
	$query = "SELECT C.id, C.regId1, U1.last_seen AS lastSeen1, C.regId2, U2.last_seen AS lastSeen2 FROM conversations C 
			 INNER JOIN users U1 ON C.user1 = U1.id 
			 INNER JOIN users U2 ON C.user2 = U2.id 
			 WHERE C.finished = 0 AND C.ready = 1";
	
	try {
		$stmt = $conn->prepare($query);
		$stmt->execute();
	} catch (Exception $e) {
		echo $e->getMessage();
		echo json_encode(array('response' => -1));
		Log::writeLog("Erro no CRONJOB: " . $e->getMessage());
		$conn = $database->disconnect();
		exit(1);
	}
	
	$result = $stmt->fetchAll(PDO::FETCH_ASSOC);
	
	
	// Closing open conversations which users are not seen in TEMPO seconds
	include_once './GCM.php';
	$gcm = new GCM();
	
	$message = array("message" => "[fechaOChatUniChat]");
	$registration_ids = array();
	
	foreach ($result as $row) {
		if (time() - strtotime($row['lastSeen1']) > TEMPO || time() - strtotime($row['lastSeen2']) > TEMPO) {
			array_push($registration_ids, $row['regId1']);
			array_push($registration_ids, $row['regId2']);
			
			$conn->query("UPDATE conversations SET ready = 1, finished = 1 WHERE id = '" . $row['id'] . "'");
			
			Log::writeLog("[CronJob] Conversa fechada. ID: " . $row['id']);
		}
	}
	
	$gcm->send_notification($registration_ids, $message);
?>