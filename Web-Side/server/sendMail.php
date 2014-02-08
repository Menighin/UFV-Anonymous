<?php
	
	// Type 1: Resend email of validation
	// Type 2: Send a bug report
	
	include 'Database.class.php';
	
		
	if (!isset($_POST['type']))
		throw new Exception ("Missing parameters");
	
	if ($_POST['type'] == 1) {
		if (!isset($_POST['username']) || !isset($_POST['hash']) || !isset($_POST['email'])) {
			throw new Exception ("Missing parameters");
			exit(1);
		}
	
		$to = $_POST['email'];
		$subject = "Bem-vindo ao UniChat!";
		$message = "Seu cadastro está quase terminado. Basta clicar no link abaixo para validar a sua conta no UniChat. Se você não fez esse cadastro favor ignorar este e-mail.
		
	http://www.unichat.com.br/validate.php?u=" . strtolower($_POST['username']) . "&h=" . $_POST['hash'];
		$from = "noreply@unichat.com.br";
		$headers = "From:" . $from;
		mail($to, $subject, $message, $headers);
		
		print 1;
	} else if ($_POST['type'] == 2) {
		if (!isset($_POST['message'])) {
			throw new Exception ("Missing parameters");
			exit(1);
		}
		
		$name = "Nome: Anônimo";
		if (isset($_POST['name']))
			$name = "Nome: " . $_POST['name'];
		
		$email = "Email: Não informado";
		if (isset($_POST['email']))
			$email = "Email: " . $_POST['email'];
		
		$subject = "Bug report";
		$message = $_POST['message'] . "\n\n" . $name . "\n" . $email;
		
		$from = "noreply@unichat.com.br";
		$headers = "From:" . $from;
		
		mail("joao.menighin@gmail.com", $subject, $message, $headers);
		mail("thiagodd.silva@gmail.com", $subject, $message, $headers);
		
		print 1;
    }
?>