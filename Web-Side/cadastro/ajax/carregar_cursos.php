<?php
    include_once '../../database.class.php';
    
    $id = $_POST['valor'];
    
    $database = new Database();
    $database->connect();
    
    $rs = $database->getCourses($id);
    
    foreach($rs as $rg) {
        echo '<option value="' . $rg['id'] . '">' . $rg['name'] . '</option>\n';
    }
?>

