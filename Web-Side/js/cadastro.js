function getCursos(valor) {
    $("#course").html("<option value='0'>Carregando...</option>");
    $("#course").load("ajax/carregar_cursos.php", {valor : valor});
}

