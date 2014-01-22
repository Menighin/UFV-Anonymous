<?php
	if (isset($_GET['word']))
		echo md5($_GET['word']);
	else
		echo "hehehe";
	
		echo '<br>';
	echo uniqid(rand(), true);
?>
