<?php
	include "Validate.class.php";
	include "Log.class.php";
	include "Database.class.php";
	$database = new Database();
	$conn = $database->connect();
	$validate = new Validate($conn, $_POST['user'], $_POST['api_key']);
	$conn = $database->disconnect();
	
	// Function to save image
	function saveMobileAttachment($imageFile, $user)
	{
		$buffer = base64_decode($imageFile);
		$fileName = date("Y_m_d_H_i_s") . "__" . $user;

		$file = fopen("images/".$fileName.".jpg", "wb");
		fwrite($file, $buffer);
		fclose($file);
		
		echo json_encode(array('response' => 1, 'imgName' => $fileName));
	}
	
	// User not logged in
	if (!$validate->isValid()) {
		echo json_encode(array('response' => -2));
	}
	// User authenticated
	else {
		if (isset($_POST['file'])) {
			saveMobileAttachment($_POST['file'], $_POST['user']);
			Log::writeLog("Upload de imagem pelo user " . $_POST['user']);
			
		} else {
			Log::writeLog("ERRO no upload de imagem pelo user " . $_POST['user']);
			echo json_encode(array('response' => -1));
		}
	}
?>