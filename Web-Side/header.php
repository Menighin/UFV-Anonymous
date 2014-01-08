<?php
	session_start();
	header("Content-Type: text/html; charset=utf-8");
?>

<!DOCTYPE html>
<html>
	<head>
		<title>UniChat</title>
		<link rel="stylesheet" href="css/style.css">
		<script type="text/JavaScript" src="js/jquery.js"></script> 
		<script type="text/JavaScript" src="js/script.js"></script> 
		<meta charset="utf-8">
		<meta name="viewport" content="width=device-width, initial-scale=1.0"/>
	</head>
	<body>
		<div id="wrapper">
			<header>
				<div id="logo">
					<a href="http://www.google.com.br"><img src="img/logo.png" /></a>
				</div>
				<div id="menuContent">
					<div id="login">
						<?php
							if (!isset($_SESSION['user'])) {
						?>
						
						<form>
							<input type="text" id="userLogin" placeholder="Usuário" /><br />
							<input type="password" id="passwordLogin" placeholder="Senha" />
						</form>
						<a class="button" onclick="login();"> Login </a>
						<a class="button" href="register.php"> Registrar </a>
						<div id="loginMsg"></div>
						<div id="forgotPassword"><a href="/forgot.php">Esqueci minha senha</a></div>
						<?php
							} else {
						?>
						<div id="logged">
							Bem-vindo <span><?php echo $_SESSION['user']; ?></span><br />
							<a href="/profile.php">Painel de controle</a> | <a href="/logout.php">Logout</a>
						</div>
						<?php
							}
						?>
					</div>
					<div id="menuWrapper">
						<img id="menutip1" src="img/menutip1.png" />
						<div id="menu">
							<a class="menuItem" href="Home">Home</a>
							<a class="menuItem" href="Estatistica">Estatásticas</a>
							<a class="menuItem" href="Reportar">Reportar Bugs</a>
						</div>
						<img id="menutip2" src="img/menutip2.png" />
					</div>
				</div>
			<header>