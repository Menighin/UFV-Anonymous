<?php

	include 'Database.class.php';
	$database = new Database();
	$conn = $database->connect();
	
	try {
		
		if (!isset($_POST['username']) || !isset($_POST['password']) || !isset($_POST['sex']) || !isset($_POST['course']))
			throw new Exception ("Missing parameters");
		
		if (strlen($_POST['password']) != 0) {
			$query = "UPDATE users SET password=:password, sex=:sex, course=:course WHERE username=:username";
			$stmt = $conn->prepare($query);
			$stmt->execute(array(
				'username' => strtolower($_POST['username']),
				'password' => strtolower($_POST['password']),
				'sex' => $_POST['sex'],
				'course' => $_POST['course'],
				));
		} else {
			$query = "UPDATE users SET sex=:sex, course=:course WHERE username=:username";
			$stmt = $conn->prepare($query);
			$stmt->execute(array(
				'username' => strtolower($_POST['username']),
				'sex' => $_POST['sex'],
				'course' => $_POST['course']
				));
		}
		
	} catch (Exception $e) {
		print -1;
		echo $e->getMessage();
		$conn = $database->disconnect();
		exit(1);
	}
	
	print 1;
    
	$conn = $database->disconnect();
?>