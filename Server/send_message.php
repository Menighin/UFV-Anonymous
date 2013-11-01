<?php
	
	header("Content-Type: text/html;charset=utf-8");
	
	include "database.class.php";
	$database = new Database();
	$conn = $database->connect();

	// Prepare query
	try {
		$stmt = $conn->prepare("INSERT INTO messages (conversation_id, message, time, author, is_read, END_FLAG) VALUES (:id, :msg, NOW(), :author, 0, :flag)");
		$stmt->execute(array(':id' => $_POST['conversation_id'], ':msg' => $_POST['message'], ':author' => $_POST['author'], ':flag' => $_POST['flag']));
	} catch (Exception $e) {
		echo json_encode(array('response' => -1));;
	}
	
	
	echo json_encode(array('response' => 1));

?>