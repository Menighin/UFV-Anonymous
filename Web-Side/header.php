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
		<meta name="viewport" content="width=device-width; initial-scale=1.0; maximum-scale=1.0; user-scalable=0;" />
	</head>
	<body>
		<div id="wrapper">
			<header>
				<div id="logo">
					<a href="http://www.unichat.com.br"><img src="img/logo.png" /></a>
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
						<div id="loadingLogin"><img src="img/loader.gif" width="25" height="25" /></div>
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
							<div class="menuItem" href="http://www.unichat.com.br">Home</div>
							<a class="menuItem" href="http://www.unichat.com.br">Estatísticas</a>
							<a class="menuItem" href="http://www.unichat.com.br">Reportar Bugs</a>
						</div>
						<img id="menutip2" src="img/menutip2.png" /> 
					</div>
					
					<div id="adTop">
						publicidade <br />
						<script async src="//pagead2.googlesyndication.com/pagead/js/adsbygoogle.js"></script>
						<ins class="adsbygoogle"
							 data-ad-client="ca-pub-7698594623226287"
							 data-ad-slot="6807286609"></ins>
						<script>
							(adsbygoogle = window.adsbygoogle || []).push({});
							</script>
					</div>
					
					<?php  include "analyticstracking.php"; ?>
					
				</div>
			<header>
