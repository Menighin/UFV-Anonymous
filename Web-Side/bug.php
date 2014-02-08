<?php
	include 'server/Database.class.php';
	include 'header.php';
?>
			<div class="loading"><img src="img/loader.gif" /> </div>
			<section id="contentWrapper">
				<div id="content">
					<form id="registerForm">
						<div id="formDiv">
							<h2> Reportar bug </h2><br />
							<span>Descrição*: </span> <br />
							<textarea id="bugDesc" required></textarea> <br /><br />
							Seu nome: <br />
							<input name="name" maxlength="40" type="text" > <br /><br />
							Seu email*: <br />
							<input name="email" maxlength="40" type="text" > <br /><br />
							<div id="reportButton" class="button" onclick="report();">Reportar</div>
						</div>
					</form>
				</div>
			</section>
			<?php
				include "footer.php"
			?>
