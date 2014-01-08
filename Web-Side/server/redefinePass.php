<?php

	include 'Database.class.php';
	$database = new Database();
	
		
	if (!isset($_POST['username']))
		throw new Exception ("Missing parameters");
	
	$conn = $database->connect();
	
	try {
		$new_pass = substr(md5(uniqid(rand(), true)), 0, 10);
		$api_key =  md5(uniqid(rand(), true));
	
		$query = "SELECT email FROM users WHERE username=:username";
		$stmt = $conn->prepare($query);
		$stmt->execute(array ('username' => $_POST['username']));
		
		$row = $stmt->fetch();
		$email = $row['email'];
		
		$query = 'UPDATE users SET password=:password, api_key=:api_key WHERE username=:username';
		$stmt = $conn->prepare($query);
		$stmt->execute(array ('username' => $_POST['username'], 'password' => $new_pass, 'api_key' => $api_key));
		
		$to = $email;
		$subject = "Senha redefinida";
		$message = 'Sua senha foi alterada atrav�s do "Esqueci minha senha" no site. Sua nova senha � ' . $new_pass .' . Para redefin�-la basta logar-se no site e ir no Painel de Controle';
		$from = "noreply@unichat.com.br";
		$headers = "From:" . $from;
		mail($to, $subject, $message, $headers);
		
		echo "Sua nova senha foi enviada ao seu e-mail";
		
		$conn = $database->disconnect();
		
	} catch (Exception $e) {
		echo "Deu treta no servidor. Tenta mais tarde fazendo um favor?<br />";
		$conn = $database->disconnect();
	}
		
?>