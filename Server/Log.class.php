<?php

class Log {
	public static function writeLog($message) {
		
		$timestamp = "[" . date("H:i:s") . "] ";
		file_put_contents ("logs/" . date("Y-m-d"). ".txt", $timestamp . $message . "\n", FILE_APPEND);
		
	}
}
?>