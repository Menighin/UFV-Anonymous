<?php
	include "Validate.class.php";
	include "Log.class.php";
	$validate = new Validate($conn, $_POST['user'], $_POST['api_key']);
	
	// Function to save image
	function saveMobileAttachment($imageFile, $user)
	{
		$buffer = base64_decode($imageFile);
		$fileName = date("H:i:s") . "_" . $user;

		$file = fopen("images/".$fileName.".jpg", "wb");
		fwrite($file, $buffer);
		fclose($file);
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
			echo json_encode(array('response' => 1));
		} else {
			Log::writeLog("ERRO no upload de imagem pelo user " . $_POST['user']);
			echo json_encode(array('response' => -1));
		}
	}
	
?>