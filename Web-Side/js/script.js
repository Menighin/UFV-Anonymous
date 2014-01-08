/* SLIDESHOW ********************************************************************************/
// Slideshow
$(function () {
	$("#slideshow li:gt(0)").hide();
	$($("#slidebuttons li:first img").attr("src", "img/home/slide_on.png"));
});

var counter = 0;

function changePictures() {
	var images = $("#slideshow li").length;

	$($("#slideshow li, #item_slideshow li").get(counter)).fadeOut(1000);
	$($("#slideshow li, #item_slideshow li").get((counter + 1)%images)).fadeIn(1000).end();
	
	$($("#slidebuttons li").get(counter)).find('img').attr("src", "img/home/slide_off.png");
	$($("#slidebuttons li").get((counter + 1)%images)).find('img').attr("src", "img/home/slide_on.png");
	
	counter++;
	if (counter > images - 1)
		counter = 0;
}

var timer = setInterval(changePictures, 5000);

// Click butttons
function chooseSlide (slide) {
	$($("#slideshow li").get(counter)).fadeOut(1000);
	$($("#slidebuttons li").get(counter)).find('img').attr("src", "img/home/slide_off.png");
	
	counter = slide;
	
	$($("#slideshow li").get(counter)).fadeIn(1000).end();
	$($("#slidebuttons li").get(counter)).find('img').attr("src", "img/home/slide_on.png");
	
	clearInterval(timer);
	timer = setInterval(changePictures, 5000);
}

// Hover buttons
$(function () { 
	$("#slidebuttons li").hover (
		function () {
			if ($(this).find('img').attr("src") == "img/home/slide_off.png")
				$(this).find('img').attr("src", "img/home/slide_hover.png");
		},
		function () {
			if ($(this).find('img').attr("src") == "img/home/slide_hover.png")
				$(this).find('img').attr("src", "img/home/slide_off.png");
		}
	);
});

/* LIGHTBOX ********************************************************************************/
$(function () {
	$("#gallery a").click( function () {
		var captRaw = $(this).attr("title").split(";");
		
		var caption = '<a target="_blank" href="/item.php?id=' + captRaw[1] + '">' + captRaw[0] + '</a>';
		$("#lightbox").fadeIn(600);
		$("#black").fadeIn();
		
		var img = new Image();
		img.src = $(this).find("img").attr("src");
		$("#imgBig").html('<a href="/item.php?id=' + captRaw[1] + '">' + '<img src="' + img.src + '" /></a>');
		
		//console.log(document.getElementById("imgBig"));
		
		//console.log('<a href="/item.php?id=' + captRaw[1] + '">''</a>');
		
		if ($("#imgBig img")[0].width > 900) {
			$("#imgBig img")[0].height = ($("#imgBig img")[0].height*900)/$("#imgBig img")[0].width;
			$("#imgBig img")[0].width = 900;
		}
		
		$("#imgAttr").html(caption);
		
		//CSS
		$("#lightbox").css("width", $("#imgBig img")[0].width + 8);
		$("#lightbox").css("height", $("#imgBig img")[0].height + 38);
		$("#lightbox").css("left", "5o%");
		$("#lightbox").css("top", "50%");
		$("#lightbox").css("margin-left", -($("#imgBig img")[0].width + 8)/2);
		$("#lightbox").css("margin-top", -($("#imgBig img")[0].height + 38)/2);
		
		$("#black, #closeButton").click( function () {
			$("#black").fadeOut();
			$("#lightbox").fadeOut(400, function () {
				$("#lightbox").css("width", "400px");
				$("#lightbox").css("height", "400px");
				$("#lightbox").css("left", "50%");
				$("#lightbox").css("top", "50%");
				$("#lightbox").css("margin-left", "-200px");
				$("#lightbox").css("margin-top", "-200px");
			});
		});
		
	});
});

/* LOGIN ********************************************************************************/
var cssBorderInputAccepted = "2px solid #94bc3e";
var cssBorderInputDenied = "2px solid #e66464"
var cssColorInputAccepted = "#94bc3e";
var cssColorInputDenied = "#e66464";


function login() {
	var username = $("#userLogin").val();
	var password = $("#passwordLogin").val();
	
	if (username.length > 0 && password.length > 0) {
		var POSTdata = {username: username, password: password};
			
			$.ajax({
				type: "POST",
				url: "server/login.php", 
				data: POSTdata,
				success: function(msg) {
					if (msg == 0) {
						$("#loginMsg").html("Usuário ou senha inválidos");
					} else if (msg == 1) {
						$("#login").html('<div id="logged">Bem-vindo <span>' + username.toLowerCase() + '</span><br /><a href="/profile.php">Painel de controle</a> | <a href="/logout.php">Logout</a></div>');
					} else {
						alert("Um error ocorreu no servidor. Por favor tente mais tarde.");
					}
				}
			});
	}
}


/* VALIDAÇÃO REGISTRO ********************************************************************************/
var validUsername = false;
var validPassword = false;
var validPasswordConfirm = false;
var validEmail = false;

function checkUsername(input) {
	if (input.value.length > 0 || validUsername) {
		if (input.value.length < 5) {
			$(input).css("border", cssBorderInputDenied);
			$(input).css("color", cssColorInputDenied);
			$("#usernameMsg").html("Usuário deve ter mais que 5 caracteres");
			validUsername = false;
		} else {
			$.ajax({
				type: "GET",
				url: "server/checkUsername.php?username=" + input.value, 
				dataType: "text",
				success: function(msg) {
					if (msg == 0) {
						$(input).css("border", cssBorderInputDenied);
						$(input).css("color", cssColorInputDenied);
						$("#usernameMsg").html("Esse usuário já existe");
						validUsername = false;
					} else if (msg == 1) {
						$(input).css("border", cssBorderInputAccepted);
						$(input).css("color", cssColorInputAccepted);
						$("#usernameMsg").html("");
						validUsername = true;
					} else {
						alert("Um error ocorreu no servidor. Por favor tente mais tarde.");
					}
				}
			});
		}
	}
}

function checkPassword (input) {
	if (input.value.length > 0 || validPassword) {
		if (input.value.length < 5) {
			$(input).css("border", cssBorderInputDenied);
			$("#passwordMsg").html("Sua senha deve ter mais que 5 caracteres");
			validPassword = false;
		} else {
			$(input).css("border", cssBorderInputAccepted);
			$("#passwordMsg").html("");
			validPassword = true;
		}
	}
}

function checkConfirmPassword (input) {
	if (validPassword || validPasswordConfirm) {
		var password = document.getElementById("password");
		if (input.value == password.value) {
			$(input).css("border", cssBorderInputAccepted);
			$("#passwordMsg").html("");
			validPasswordConfirm = true;
		} else {
			$(input).css("border", cssBorderInputDenied);
			$("#passwordMsg").html("A senha e confirmação são diferentes");
			validPasswordConfirm = false;
		}
	}
}

function onChangeUniveristy (university) {
	$.ajax({
		type: "GET",
		url: "server/changeUniversity.php?id=" + university.value, 
		dataType: "json",
		success: function(msg) {
			$("#coursesList").html("");
			for (var i = 0; i < msg.length; i++) 
				$("#coursesList").append('<option value="' + msg[i]['id'] + '">' + msg[i]['name'] + '</option>');
			
			if (msg.length > 0)
				$("#email").html(msg[0]['email']);
			
			// Checando novo email
			var email = $("input[name=email]").val();
	
			if (email.length > 0 || validEmail) {
				$.ajax({
					type: "GET",
					url: "server/checkEmail.php?email=" + email + $("#email").html(), 
					dataType: "text",
					success: function(msg) {
						if (msg == 0) {
							$("input[name=email]").css("border", cssBorderInputDenied);
							$("input[name=email]").css("color", cssColorInputDenied);
							$("#emailMsg").html("Este email já está em uso");
							validEmail = false;
						} else if (msg == 1) {
							$("input[name=email]").css("border", cssBorderInputAccepted);
							$("input[name=email]").css("color", cssColorInputAccepted);
							$("#emailMsg").html("");
							validEmail = true;
						} else {
							alert("Um error ocorreu no servidor. Por favor tente mais tarde.");
						}
					}
				});
			}
		}
	});
	
}

function checkEmail(input) {
	if (input.value.length == 0) {
		$("#emailMsg").html("");
	} else if (input.value.length > 0 || validEmail) {
		$.ajax({
			type: "GET",
			url: "server/checkEmail.php?email=" + input.value + $("#email").html(), 
			dataType: "text",
			success: function(msg) {
				if (msg == 0) {
					$(input).css("border", cssBorderInputDenied);
					$(input).css("color", cssColorInputDenied);
					$("#emailMsg").html("Este email já está em uso");
					validEmail = false;
				} else if (msg == 1) {
					$(input).css("border", cssBorderInputAccepted);
					$(input).css("color", cssColorInputAccepted);
					$("#emailMsg").html("");
					validEmail = true;
				} else {
					alert("Um error ocorreu no servidor. Por favor tente mais tarde.");
				}
			}
		});
	}
}

function register() {
	if(!validUsername)
		$("input[name=username]").focus();
	else if (!validPassword)
		$("input[name=password]").focus();
	else if (!validPasswordConfirm)
		$("input[name=confirmPassword]").focus();
	else if (!validEmail)
		$("input[name=email]").focus();
	else {
		var username = $("input[name=username]").val();
		var password = $("#password").val();
		var email = $("input[name=email]").val() + $("#email").html();
		var sex = $("select[name=sex]").val();
		var university = $("select[name=university]").val();
		var course = $("select[name=course]").val();
		
		var POSTdata = {username: username, password: password, email: email, sex: sex, university: university, course: course};
		
		$.ajax({
			type: "POST",
			url: "server/register.php", 
			data: POSTdata,
			success: function(msg) {
				if (msg == 1) {
					$("#formDiv").html('<div id="successMsg">Seu usuário foi cadastrado com sucesso!<br>Um e-mail de confirmação foi enviado para <span>' + email + '</span>.</div>');
				} else {
					alert("Um error ocorreu no servidor. Por favor tente mais tarde.");
				}
			}
		});
		
	}
}

/* PROFILE ********************************************************************************/
function resendMail() {
	var username = $("input[name=username]").val();
	var email = $("input[name=email]").val();
	var hash = $("input[name=hash]").val();
	
	var POSTdata = {username: username, email: email, hash: hash};
	$.ajax({
		type: "POST",
		url: "server/sendMail.php", 
		data: POSTdata,
		success: function(msg) {
			if (msg == 1) {
				$("#content").html('<div id="successMsg">Um novo email foi enviado para <span>' + email + '</span>.</div>');
			} else {
				alert("Um error ocorreu no servidor. Por favor tente mais tarde.");
			}
		}
	});
}

var somethingChanged = false;

function changeProfile() {
	somethingChanged = true;
	$("#saveButton").css("opacity", "1");
}

function updateUser() {
	if (somethingChanged) {
		if (!validPassword && $("#password").val().length != 0)
			$("input[name=password]").focus();
		else if (!validPasswordConfirm && $("#password").val().length != 0)
			$("input[name=confirmPassword]").focus();
		else {
			var username = $("#username").val();
			var password = $("#password").val();
			var sex = $("select[name=sex]").val();
			var course = $("select[name=course]").val();
			
			var POSTdata = {username: username, password: password, sex: sex, course: course};

			$.ajax({
				type: "POST",
				url: "server/updateUser.php", 
				data: POSTdata,
				success: function(msg) {
					if (msg == 1) {
						$("#updateSuccess").html('Usuário atualizado com sucesso');
						somethingChanged = false;
						$("#saveButton").css("opacity", "0.5");
					} else {
						alert("Um error ocorreu no servidor. Por favor tente mais tarde.");
					}
				}
			});
		}
	}
}

/* ESTATÍSTICAS ********************************************************************************/
var myColor = ["#ECD078","#D95B43","#C02942","#542437","#53777A"];
var myData = [10,30,20,60,40];
var center_x = 200;
var center_y = 150;
var radius = 126;

function getTotal(){
	var myTotal = 0;
	for (var j = 0; j < myData.length; j++) {
		myTotal += (typeof myData[j] == 'number') ? myData[j] : 0;
	}
	return myTotal;
}

function plotData() {
	var canvas;
	var ctx;
	var lastend = 0;
	var myTotal = getTotal();

	canvas = document.getElementById("generalSex");
	ctx = canvas.getContext("2d");
	ctx.clearRect(0, 0, canvas.width, canvas.height);

	for (var i = 0; i < myData.length; i++) {
		var middle = lastend + (Math.PI*(myData[i]/myTotal));
	
		ctx.fillStyle = myColor[i];
		ctx.beginPath();
		ctx.arc(center_x, center_y, radius, lastend, lastend + (Math.PI*2*(myData[i]/myTotal)), false);
		ctx.lineTo(center_x, center_y);
		ctx.fill();
		
		lastend += Math.PI*2*(myData[i]/myTotal);
		
		var y_dist = Math.sin(middle) * radius;
		var x_dist = Math.cos(middle) * radius;
		
		// Calculando reta => y - center_y = m * (x - center_x)
		var m = y_dist / x_dist;
		
		var d = 10;
		var x0 = center_x + x_dist;
		var y0 = center_y + y_dist;
		var x, y;
		if (x0 < center_x) {
			x = x0 - d / Math.sqrt(m*m + 1);
			y = y0 - (d * m) / Math.sqrt(m * m + 1);
		} else {
			x = x0 + d / Math.sqrt(m*m + 1);
			y = y0 + (d * m) / Math.sqrt(m * m + 1);
		}
		
		
		ctx.beginPath();
		ctx.moveTo(center_x + x_dist, center_y + y_dist);
		ctx.lineTo(x, y);
		ctx.stroke();
		
		ctx.save();
		ctx.fillStyle = "#000";
		ctx.rotate(Math.atan(m));
		//ctx.beginPath();
		ctx.font = "12px Arial";
		//ctx.fillText("Testando", x, y );
		ctx.fillRect(x, y, 10, 5);
		ctx.restore();
		
	}
}

$(function () {
	plotData();
});