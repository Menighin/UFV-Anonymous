<?php

	include 'Database.class.php';
	$database = new Database();
	$conn = $database->connect();
	
	if (strlen($_POST['param']) > 0)
		$query = "SELECT SUM(CASE WHEN sex = 'm' THEN 1 END) AS M, SUM(CASE WHEN sex = 'f' THEN 1 END) AS F FROM users WHERE " . $_POST['param'] . "='" . $_POST['attr'] . "'";
	else
		$query = "SELECT SUM(CASE WHEN sex = 'm' THEN 1 END) AS M, SUM(CASE WHEN sex = 'f' THEN 1 END) AS F FROM users";
	
	try {
		$stmt = $conn->prepare($query);
		$stmt->execute();
	} catch (Exception $e) {
		$conn = null;
		exit(1);
	}
			
	$row = $stmt->fetch();
	
	echo json_encode($row);
	
?>
	