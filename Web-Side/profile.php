<?php
	include "header.php";
	include "server/Database.class.php";
	include "analyticstracking.php";
	header("Content-Type: text/html; charset=utf-8");
	
	if (!isset($_SESSION['user']))
		header("Location: ..");
	
	$database = new Database();
	$conn = $database->connect();
	
	try {
		$query = 'SELECT U.username, U.email, U.sex, U.university, U.course, U.valid, UNI.name
					FROM users AS U INNER JOIN universities AS UNI ON U.university = UNI.id 
					WHERE U.username=:user';
		$stmt = $conn->prepare($query);
		$stmt->execute(array ('user' => $_SESSION['user']));
		
		$user = $stmt->fetch();
		
		if (!$user)
			throw new Exception ("User doesn't exists!");
		
	} catch (Exception $e) {
		echo "Deu treta no servidor. Tenta mais tarde fazendo um favor?<br />";
		$conn = $database->disconnect();
	}
?>

			<section id="contentWrapper">
				<div id="content">
					<?php
						if ($user && $user['valid'] == true) {
					?>
					<form id="registerForm">
						<div id="formDiv">
							<input type="hidden" id="username" value="<?php echo $user['username']; ?>" />
							<h1 id="usernameProfile"><?php echo $user['username']; ?></h1>
							<h2><?php echo $user['email']; ?></h2>
							
							<br /><br />
							
							<table>
								<tr>
									<td>
										Nova senha: <br />
										<input id="password" name="password" maxlength="15" type="password" onblur="checkPassword(this); changeProfile();" /> <br />
									</td>
									<td>
										Confirmar nova senha: <br />
										<input name="confirmPassword" maxlength="15" type="password" onblur="checkConfirmPassword(this); changeProfile();" /> <br />
									</td>
									<td>
										<span id="passwordMsg"></span>
									</td>
								</tr>
							</table><br />
							
							Sexo: <br />
							<select name="sex" onchange="changeProfile();">
								<option value="m" <?php if ($user['sex'] == "m") echo "selected"; ?>>Masculino</option>
								<option value="f" <?php if ($user['sex'] == "f") echo "selected"; ?>>Feminino</option>
							</select><br /><br />
							
							Universidade:<br />
							<h4><?php echo $user['name'];?></h4><br />
							
							
							Curso:<br />
							<?php
								try {
									$stmt = $conn->prepare('SELECT * FROM courses WHERE university_id = ' . $user['university'] . ' ORDER BY name ASC');
									$stmt->execute();
									
									$result= $stmt->fetchAll();
									
									echo '<select name="course" id="coursesList" onchange="changeProfile();">';
										
										foreach ($result as $row) {
											echo '<option value="' . $row['id'] . '"';
											if ($user['course'] == $row['id'])
												echo " selected";
											echo '>' . $row['name'] . '</option>';
										}
										
									echo "</select>";
									
									$conn = $database->disconnect();
								} catch (Exception $e) {
									echo "Deu treta no servidor. Tenta mais tarde fazendo um favor?<br />";
									$conn = $database->disconnect();
								}
							?><br />
							<br />
							
							<div id="saveButton" onclick="updateUser();" class="button">Salvar</div>
							<br /> <br /><br />
							<div id="updateSuccess"></div>
						
						</div>
					</form>
					
					
					
					<?php
						} else if ($user) {
					?>
						<div id="successMsg">
							Esse usuário ainda não foi validado!<br />
							Verifique o email <span><?php echo $user['email'] ?></span> (inclusive lixeira, spam, etc). <br /><br />
							
							<div class="button" style="display: inline;" onclick="resendMail();">Reenviar e-mail</div>
							<input type="hidden" name="username" value="<?php echo $user['username']; ?>" />
							<input type="hidden" name="email" value="<?php echo $user['email']; ?>" />
							<input type="hidden" name="hash" value="<?php echo $user['hash']; ?>" />
						</div>
					<?php
						}
					?>
				
			
				
				</div>
			</section>
			
<?php
	$conn = $database->disconnect();
	include "footer.php"
?>
