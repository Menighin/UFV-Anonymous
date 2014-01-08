<?php
	include "header.php";
	header("Content-Type: text/html; charset=utf-8");
?>

			<section id="contentWrapper">
				<div id="content">
					<form id="registerForm" action="server/redefinePass.php" method="POST">
						<div id="formDivForgot">
							<span>Usuário:</span> <br />
							<input name="username" type="text" /> <br />
							Uma mensagem com a sua nova senha será enviada para o seu e-mail.<br />
							<input type="submit" value="Enviar" />
							
						</div>
					</form>
				</div>
			</section>
			
<?php
	include "footer.php"
?>