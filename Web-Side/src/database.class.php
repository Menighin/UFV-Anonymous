<?php

class Database {

    private $conn = null;
    private $username = 'california';
    private $password = '123456';
    private $host = 'localhost';
    private $dbname = 'anonymouschat';

    /* Abre uma conexão com o banco de dados. */

    function connect() {
        try {
            //$this->conn = new PDO("mysql:host=" . $this->host . ";dbname=" . $this->dbname, 
             //                       $this->username, $this->password);
            
            $this->conn = new PDO("mysql:host=localhost;dbname=anonymouschat", "root", "california");
            
            $this->conn->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
            $this->conn->exec('SET CHARACTER SET utf8');
            $this->conn->exec('SET NAMES utf8');
            
        } catch (PDOException $ex) {
            echo 'Erro ao se conectar com o banco de dados.';
            echo $ex;
        }

        return $this->conn;
    }

    function disconnect() {
        $this->conn = null;
        return $this->conn;
    }
    
    /* Retorna a lista de faculdades gravada no banco de dados. */
    function getUniversities() {
        $resultSet = $this->conn->query("SELECT * FROM universities")->fetchall();
        return $resultSet;
    }
    
    function getCourses($university_id) {
        $resultSet = $this->conn->query("SELECT * FROM courses WHERE university_id = " . $university_id)
                ->fetchall();
        return $resultSet;
    }
    
    function insertUser($sql) {
        
    }
}

?>
