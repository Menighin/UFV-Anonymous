<?php

	header("Content-Type: text/html; charset=utf-8");

	include "database.class.php";
	$database = new Database();
	$conn = $database->connect();

	// Prepare query
	$stmt = $conn->prepare('SELECT * FROM messages WHERE conversation_id=:id AND is_read = 0 AND author = :author');
	$stmt->execute(array(':id' => $_POST['conversation_id'], ':author' => $_POST['author']));
	
	// Fetch
	$result= $stmt->fetchAll();
	
	foreach ($result as $row)
		$conn->query("UPDATE messages SET is_read = 1 WHERE id = '" . $row['id'] . "'");
	
	if (!$result)
		echo json_encode(array('response' => 0));
	else
		echo json_encode(array('response' => 1, 'messages' => $result));

?>