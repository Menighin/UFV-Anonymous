<?php
	include 'server/Database.class.php';
	include 'header.php';
?>

			<section id="contentWrapper">
				<div id="content">
					<form id="registerForm">
						<div id="formDiv">
							<span>Usuário: </span> <br />
							<input name="username" type="text" maxlength="15" onblur="checkUsername(this);" autocomplete="off" /> <span id="usernameMsg"></span> <br /><br />
							<table>
								<tr>
									<td>
										Senha: <br />
										<input id="password" name="password" maxlength="15" type="password" onblur="checkPassword(this);" /> <br />
									</td>
									<td>
										Confirmar senha: <br />
										<input name="confirmPassword" maxlength="15" type="password" onblur="checkConfirmPassword(this);" /> <br />
									</td>
									<td>
										<span id="passwordMsg"></span>
									</td>
								</tr>
							</table><br />
							Sexo: <br />
							<select name="sex">
								<option value="m">Masculino</option>
								<option value="f">Feminino</option>
							</select><br /><br />
							<table>
								<tr>
									<td>
										Universidade:<br />
										<?php
											$database = new Database();
											$conn = $database->connect();
											
											try {
												$stmt = $conn->prepare('SELECT * FROM universities ORDER BY id ASC');
												$stmt->execute();
												
												$result= $stmt->fetchAll();
												
												$uniID = $result[0]['id']; // Pega ID da primeira universidade (a que fica de default no select)
												
												echo '<select name="university" onchange="onChangeUniveristy(this);">';
												
												foreach ($result as $row) 
													echo '<option value="' . $row['id'] . '">' . $row['acronym'] . '</option>';
													
												echo "</select>";
												
											} catch (Exception $e) {
												echo "Deu treta no servidor. Tenta mais tarde fazendo um favor?<br />";
												$uniID = null;
												$conn = $database->disconnect();
											}
											
										?>
									</td>
									<td>Curso:<br />
										<?php
											if ($uniID != null) {
												try {
													$stmt = $conn->prepare('SELECT * FROM courses WHERE university_id = ' . $uniID . ' ORDER BY name ASC');
													$stmt->execute();
													
													$result= $stmt->fetchAll();
													
													echo '<select name="course" id="coursesList">';
														
														foreach ($result as $row) 
															echo '<option value="' . $row['id'] . '">' . $row['name'] . '</option>';
															
													echo "</select>";
													
													$conn = $database->disconnect();
												} catch (Exception $e) {
													echo "Deu treta no servidor. Tenta mais tarde fazendo um favor?<br />";
													$conn = $database->disconnect();
												}
											}
										?>
									</td>
								</tr>
							</table><br />
							E-mail: <br />
							<input name="email" maxlength="40" type="text" onblur="checkEmail(this);" required ><span id="email">@ufv.br</span> <span id="emailMsg"></span> <br /><br />
							<div id="registerButton" onclick="register();" class="button">Registrar</div>
							
						</div>
					</form>
				</div>
			</section>
			<?php
				include "footer.php"
			?>