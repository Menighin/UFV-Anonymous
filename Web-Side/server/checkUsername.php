<?php

	include 'Database.class.php';
	$database = new Database();
	$conn = $database->connect();
	
	try {
		$stmt = $conn->prepare('SELECT * FROM users WHERE username=:username');
		$stmt->execute(array('username' => strtolower($_GET['username'])));
	} catch (Exception $e) {
		print -1;
		$conn = $database->disconnect();
		exit(1);
	}
	
    $row = $stmt->fetch();
	if (!$row)
		print 1; // Usu�rio n�o existe
	else {
		print 0; // Usu�rio j� existe
	}
    
	$conn = $database->disconnect();
?>