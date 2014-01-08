<?php

	include 'server/Database.class.php';
	include ('header.php');

?>

			<section id="contentWrapper">
				<div id="content">
					<div id="registerForm">
						<div id="formDiv">
							<div id="successMsg">
								<?php
									$database = new Database();
									$conn = $database->connect();
									
									try {
										if (!isset($_GET['u']) || !isset($_GET['h']))
											throw new Exception("Missing parameters");
										
										$query = "UPDATE users SET valid = 1 WHERE username = :username AND hash = :hash";
										
										$stmt = $conn->prepare($query);
										
										$stmt->execute(array('username' => strtolower($_GET['u']), 'hash' => $_GET['h']));
										
										echo "Usuário validado com sucesso!";
										
										$conn = $database->disconnect();
										
										header("refresh:5;url=.." );
										
									} catch (Exception $e) {
										echo "Ocorreu uma falha no servidor, o usuário não foi validado.";
										$conn = $database->disconnect();
										exit(1);
									}
									
								?>
							</div>
						</div>
					</div>
				</div>
			</section>
			<?php
				include "footer.php"
			?>