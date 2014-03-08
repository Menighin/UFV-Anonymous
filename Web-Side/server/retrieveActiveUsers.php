<?php

	include 'Database.class.php';
	$database = new Database();
	$conn = $database->connect();
	
	$query = "SELECT COUNT(*) AS active FROM users WHERE TO_SECONDS(NOW()) - TO_SECONDS(last_seen) <= 600";
	
	try {
		$stmt = $conn->prepare($query);
		$stmt->execute();
	} catch (Exception $e) {
		$conn = null;
		exit(1);
	}
			
	$row = $stmt->fetch();
	
	echo '<div id="activeUsers">' . $row['active'] . ' usuários ativos nos últimos 10 minutos</div>';
	
	$conn = $database->disconnect();
	
?>
	