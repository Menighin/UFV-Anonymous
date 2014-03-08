<?php
	
	/* Parametros
	0 => */
	
	include 'Database.class.php';
	$database = new Database();
	$conn = $database->connect();
	header("Content-Type: text/html; charset=utf-8");
	
	if($_POST['func'] == 0) {
		$query = "SELECT universities.acronym AS University, SUM(CASE WHEN sex = 'm' THEN 1 ELSE 0 END) AS M, SUM(CASE WHEN sex = 'f' THEN 1 ELSE 0 END) AS F 
		          FROM users INNER JOIN universities ON users.university=universities.id
		          GROUP BY university";
	} else if (isset($_POST['uni'])) {
		if ($_POST['func'] == 1) {
			$query = "SELECT courses.name AS Course, courses.acronym AS Acronym, SUM(CASE WHEN sex = 'm' THEN 1 ELSE 0 END) AS M, SUM(CASE WHEN sex = 'f' THEN 1 ELSE 0 END) AS F 
					  FROM users INNER JOIN courses ON users.course=courses.id WHERE users.university = " . $_POST['uni'] . " 
					  GROUP BY course";
		} else if ($_POST['func'] == 2) {
			$query = "SELECT COUNT(*) AS active FROM users WHERE TO_SECONDS(NOW()) - TO_SECONDS(last_seen) <= 300 AND university = " . $_POST['uni'];
		}
		
	}
	
	try {
		$stmt = $conn->prepare($query);
		$stmt->execute();
	} catch (Exception $e) {
		$conn = null;
		exit(1);
	}
			
	$result = $stmt->fetchAll(PDO::FETCH_ASSOC);
	
	echo json_encode($result);

	$conn = $database->disconnect();

?>