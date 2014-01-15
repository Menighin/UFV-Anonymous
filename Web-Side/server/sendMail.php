<?php

	include 'Database.class.php';
	
		
	if (!isset($_POST['username']) || !isset($_POST['hash']) || !isset($_POST['email']))
		throw new Exception ("Missing parameters");
		
	$to = $_POST['email'];
	$subject = "Bem-vindo ao UniChat!";
	$message = "Seu cadastro está quase terminado. Basta clicar no link abaixo para validar a sua conta no UniChat. Se você não fez esse cadastro favor ignorar este e-mail.
	
http://www.unichat.com.br/validate.php?u=" . strtolower($_POST['username']) . "&h=" . $_POST['hash'];
	$from = "noreply@unichat.com.br";
	$headers = "From:" . $from;
	mail($to, $subject, $message, $headers);
	
	print 1;
    
?>