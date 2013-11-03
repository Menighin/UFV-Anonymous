<?php
    include_once '../database.class.php';
    include_once '../../model/User.php';

    $username = $_POST['username'];
    $password = $_POST['password'];
    $sex = $_POST['sex'];
    $email = $_POST['email'];
    $university = $_POST['university'];
    $course = $_POST['course'];
    
    $user = new User();
    $user->setUserName($username);
    $user->setPassword($password);
    $user->setSex($sex);
    $user->setEmail($email);
    $user->setUniversity($university);
    $user->setCourse($course);
    
    $database = new Database();
    $conn = $database->connect();
    
    $user->configureConnection($conn);
    
    if($user->insert()) {
        echo 'Usuário inserido';
    } else {
        echo $user->getErrorMessage();
    }
   
?>
