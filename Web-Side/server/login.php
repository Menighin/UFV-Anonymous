<?php
	include 'Database.class.php';
	session_start();
	$database = new Database();
	$conn = $database->connect();
	
	try {
		$stmt = $conn->prepare('SELECT * FROM users WHERE username=:username');
		$stmt->execute(array('username' => strtolower($_POST['username'])));
	} catch (Exception $e) {
		print -1; // Error
		$conn = $database->disconnect();
		exit(1);
	}
	
    $row = $stmt->fetch();
	if (!$row)
		print 0; // User doesn't exists
	else {
		if (strcmp($row['password'], md5(strtolower($_POST['password']))) == 0) {
			$_SESSION['user'] = strtolower($_POST['username']);
			print 1; // Ok!
		} else {
			print 0; // Wrong password
		}
	}
    
	$conn = $database->disconnect();
?>