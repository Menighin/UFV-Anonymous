<?php
class User {
    private $id;
    
    private $usermane;
    private $password;
    private $sex;
    private $email;
    private $university;
    private $course;
    
    private $errorMessage;
    
    private $conn;
    
    public function configureConnection($conn) {
        $this->conn = $conn;
    }
    
    public function insert() {
        try {
            $stm = $this->conn->prepare("INSERT INTO users (username, password, email, "
                                        . "sex, university, course) VALUES (?,?,?,?,?,?)");
            $stm->bindParam(1, $this->usermane, PDO::PARAM_STR);
            $stm->bindParam(2, $this->password, PDO::PARAM_STR);
            $stm->bindParam(3, $this->email, PDO::PARAM_STR);
            $stm->bindParam(4, $this->sex, PDO::PARAM_STR);
            $stm->bindParam(5, $this->university, PDO::PARAM_INT);
            $stm->bindParam(6, $this->course, PDO::PARAM_INT);

            $sucesso = $stm->execute();
            if(!$sucesso) {
                $this->errorMessage = 'Erro ao inserir os dados no Banco de Dados.';
            }
            
        }  catch (Exception $ex) {
            $this->errorMessage = $ex->getMessage();
            return false;
        }
        return $sucesso;
    }
    
    public function getById($id) {
        
    }
    
    public function validEmail($email) {
        
    }
    
    public function validUserName($username) {
        
    }    
    
    /* Métodos encapsuladores. */
    public function getId() {
        return $this->id;
    }
    
    public function getUserName() {
        return $this->usermane;
    }
    public function setUserName($username) {
        $this->usermane = $username;
    }
    
    public function getPassword() {
        return $this->password;
    }
    public function setPassword($password) {
        $this->password = $password;
    }
    
    public function getSex() {
        return $this->sex;
    }
    public function setSex($sex) {
        $this->sex = $sex;
    }
    
    public function getEmail() {
        return $this->email;
    }
    public function setEmail($email) {
        $this->email = $email;
    }
    
    public function getUniversity() {
        return $this->university;
    }
    public function setUniversity($university) {
        $this->university = $university;
    }
    
    public function getCourse() {
        return $this->course;
    }
    public function setCourse($course) {
        $this->course = $course;
    }
    
    public function getErrorMessage() {
        $this->errorMessage;
    }
}
