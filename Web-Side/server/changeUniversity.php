<?php
	include 'Database.class.php';
	$database = new Database();
	$conn = $database->connect();
	
	try {
		$stmt = $conn->prepare('SELECT C.id, C.name, U.email FROM courses AS C INNER JOIN universities AS U ON C.university_id = U.id WHERE university_id =:university_id ORDER BY C.name ASC ');
		$stmt->execute(array('university_id' => ($_GET['id'])));
	} catch (Exception $e) {
		print -1;
		$conn = $database->disconnect();
		exit(1);
	}
	
    $result = $stmt->fetchAll();
	print json_encode($result);
    
	$conn = $database->disconnect();
?>