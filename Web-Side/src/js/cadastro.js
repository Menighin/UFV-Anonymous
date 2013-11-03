$(document).ready(function() {
    var password;
    var password_confirm;
    
    //Desabilita o botão de cadastro.
    $("#button-cadastrar").attr("disabled", true);
    
    //Função executada quando o campo password sai de foco.
    $("#password").change(function() {
        password = $( this ).val();
        
        if(password !== password_confirm) {
            $("#erro-password").text('Campos senha não são iguais');
        } else {
            $("#erro-password").text('');
            $("#button-cadastrar").attr("disabled", false);
        }
    });
    
    //Função executada quando o campo password confirm sai de foco.
    $("#password-confirm").change(function() {
        password_confirm = $( this ).val();
        if(password !== password_confirm) {
            $("#erro-password").text('Campos senha não são iguais');
        } else {
            $("#erro-password").text('');
            $("#button-cadastrar").attr("disabled", false);
        }
    });
    
});

function getCursos(valor) {
    $("#course").html("<option value='0'>Carregando...</option>");
    $("#course").load("ajax/carregar_cursos.php", {valor : valor});
}
    