<?php
	require 'PHPMailerAutoload.php';

	$mail = new PHPMailer;

	$mail->isSMTP();                                      // Set mailer to use SMTP
	$mail->Host = 'smtp.gmail.com'; 					  // Specify main and backup server
	$mail->SMTPAuth = true;                               // Enable SMTP authentication
	$mail->Username = 'jswan';                            // SMTP username
	$mail->Password = 'secret';                           // SMTP password
	$mail->SMTPSecure = 'tls';                            // Enable encryption, 'ssl' also accepted

	$mail->WordWrap = 50;                                 // Set word wrap to 50 characters

?>