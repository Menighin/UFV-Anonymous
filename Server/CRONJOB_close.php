<?php

	header("Content-Type: application/json; charset=utf-8");
	
	define('TEMPO', 177);
	
	set_include_path('/var/www/UniChatApi');
	
	include "Database.class.php";
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
	
	// Selecting "left over" conversations
	$query2 = "SELECT C.id, C.regId1, U1.last_seen AS lastSeen1 FROM conversations C 
			 INNER JOIN users U1 ON C.user1 = U1.id 
			 WHERE C.finished = 0";
	
	try {
		$stmt = $conn->prepare($query);
		$stmt2 = $conn->prepare($query2);
		$stmt->execute();
		$stmt2->execute();
	} catch (Exception $e) {
		echo $e->getMessage();
		echo json_encode(array('response' => -1));
		Log::writeLog("Erro no CRONJOB: " . $e->getMessage());
		$conn = $database->disconnect();
		exit(1);
	}
	
	$result = $stmt->fetchAll(PDO::FETCH_ASSOC);
	$result2 = $stmt2->fetchAll(PDO::FETCH_ASSOC);
	
	// Closing open conversations which users are not seen in TEMPO seconds
	include 'GCM.php';
	$gcm = new GCM();
	
	$message = array("message" => "[fechaOChatUniChat]");
	$registration_ids = array();
	
	// Open conversation with 2 people
	foreach ($result as $row) {
		if (time() - strtotime($row['lastSeen1']) > TEMPO || time() - strtotime($row['lastSeen2']) > TEMPO) {
			array_push($registration_ids, $row['regId1']);
			array_push($registration_ids, $row['regId2']);
			$conn->query("UPDATE conversations SET ready = 1, finished = 1 WHERE id = '" . $row['id'] . "'");
			Log::writeLog("[CronJob] Conversa fechada. ID: " . $row['id']);
		}
	}
	
	// With one people
	foreach ($result2 as $row) {
		if (time() - strtotime($row['lastSeen1']) > TEMPO) {
			array_push($registration_ids, $row['regId1']);
			$conn->query("UPDATE conversations SET ready = 1, finished = 1 WHERE id = '" . $row['id'] . "'");
			Log::writeLog("[CronJob] Conversa fechada. ID: " . $row['id']);
		}
	}
	
	$gcm->send_notification($registration_ids, $message);
?>