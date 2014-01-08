<?php

	include 'Database.class.php';
	$database = new Database();
	$conn = $database->connect();
	
	try {
		$stmt = $conn->prepare('SELECT * FROM users WHERE email=:email');
		$stmt->execute(array('email' => strtolower($_GET['email'])));
	} catch (Exception $e) {
		print -1;
		$conn = $database->disconnect();
		exit(1);
	}
	
    $row = $stmt->fetch();
	if (!$row)
		print 1; // Email não existe
	else {
		print 0; // Email já existe
	}
    
	$conn = $database->disconnect();
?>