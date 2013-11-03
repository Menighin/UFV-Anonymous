<?php 
    include_once '../database.class.php';
    
    $database = new Database();
    $database->connect();
?>
<html>
    <head>
        <meta charset="utf-8" />
        
        <title> Home Page </title>
        
        <script type="text/javascript" src="../js/jquery.js"></script>
        <script type="text/javascript" src="../js/cadastro.js"></script>
    </head>

    <body>
        <form id="form" action="processa_cadastro.php" method="POST">
            <label for="username">Nome de Usuário: </label>
            <input type="text" name="username" id="username" />
            <br />

            <label for="password">Senha: </label>
            <input type="password" name="password" id="password" />
            <br />

            <label for="password-confirm">Confirmação da Senha: </label>
            <input type="password" name="password-confirm" id="password-confirm" />
            <br />
            
            <label for="sex">Sexo: </label>
            <select>
                <option value="M">Homem</option>
                <option value="F">Mulher</option>
            </select>
            <br />

            <label for="email">Email: </label>
            <input type="text" name="email" id="email" />
            <br />

            <label for="university">Faculdade: </label>
            <select id="university" onchange="getCursos(this.value)">
                <option value="-1">Selecione...</option>
                <!-- Carregar a lista de faculdades -->
                <?php
                    $rs = $database->getUniversities();
                    /* Cria um option para cada registro no banco. */
                    foreach($rs as $registro) {
                        echo '<option value="' . $registro['id'] . '">' . $registro['name'] . '</option>';
                    }
                ?>
            </select>
            <br />
            
            <label for="course">Curso: </label>
            <select id="course">
                <option value="-1">Selecione...</option>
            </select>
            <br />
            
            <input type="submit" value="Cadastrar" />

        </form>
    </body>
</html>
