<?php

	include 'Database.class.php';
	$database = new Database();
	$conn = $database->connect();
	
	try {
		
		if (!isset($_POST['username']) || !isset($_POST['password']) || !isset($_POST['email']) || !isset($_POST['sex']) || !isset($_POST['university']) || !isset($_POST['course']))
			throw new Exception ("Missing parameters");
		
		$query = "INSERT INTO users (username, password, api_key, email, sex, university, course, hash) 
			VALUES (:username, :password, :api_key, :email, :sex, :university, :course, :hash)";
		
		
		$stmt = $conn->prepare($query);
		
		$hash = md5(uniqid(rand(), true));
		
		$stmt->execute(array(
			'username' => strtolower($_POST['username']),
			'password' => strtolower($_POST['password']),
			'api_key' => md5(uniqid(rand(), true)),
			'email' => strtolower($_POST['email']),
			'sex' => $_POST['sex'],
			'university' => $_POST['university'],
			'course' => $_POST['course'],
			'hash' => $hash
			));
	} catch (Exception $e) {
		print -1;
		$conn = $database->disconnect();
		exit(1);
	}
	
	$to = $_POST['email'];
	$subject = "Bem-vindo ao UniChat!";
	$message = "Seu cadastro está quase terminado. Basta clicar no link abaixo para validar a sua conta no UniChat. Se você não fez esse cadastro favor ignorar este e-mail.
	
				localhost/validate.php?u=" . strtolower($_POST['username']) . "&h=" . $hash;
	$from = "noreply@unichat.com.br";
	$headers = "From:" . $from;
	mail($to, $subject, $message, $headers);
	
	print 1;
    
	$conn = $database->disconnect();
?>