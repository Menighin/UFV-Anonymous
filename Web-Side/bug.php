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
							<textarea name="desc" required></textarea> <br /><br />
							Seu nome: <br />
							<input name="name" maxlength="40" type="text" > <br /><br />
							<div id="registerButton" class="button">Reportar</div>
						</div>
					</form>
				</div>
			</section>
			<?php
				include "footer.php"
			?>
