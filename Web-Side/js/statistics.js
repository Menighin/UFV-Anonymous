/* ESTATÍSTICAS ********************************************************************************/
var clickHelp = null;
var barCounter = -1;
var actualUni = -1;
var barData = { labels : [], courses : [], data1 : [], data2 : [] };
var barDataShow = {
	labels : [],
	datasets : [
		{
			fillColor : "rgba(144,199,226,0.5)",
			strokeColor : "rgba(144,199,226,1)",
			data : []
		},
		{
			fillColor : "rgba(234,150,231,0.5)",
			strokeColor : "rgba(205,151,203,1)",
			data : []
		}
	]
};

var lineData = {
	max : 0,
	labels : ["-"],
	datasets : [
		{
			fillColor : "rgba(174,42,42,0.5)",
			strokeColor : "rgba(174,42,42,1)",
			pointColor : "rgba(124,15,15,1)",
			pointStrokeColor : "rgba(174,42,42,1)",
			data : [0]
		}
	]
};

var colorsSex = ["rgba(144,199,226,1)", "rgba(234,150,231,1)"];
var colorsRed = ["#a40505", "#d12a2a", "#e55858", "#f58888", "#fcbebe"];

var options = {
	segmentShowStroke : true,
	segmentStrokeWidth : 1,
	onAnimationComplete : null
}

var timerLineGraph = setInterval(updateUsers, 5000);

function updateUsers (animation) {
	if (actualUni == -1) return;
	
	var POSTdata = {func: 2, uni: actualUni};
	$.ajax({
		type: "POST",
		url: "server/retrieveStatistics.php", 
		contentType: "application/x-www-form-urlencoded;charset=UTF-8",
        dataType: 'json',
		data: POSTdata,
		success: function(json) {
			if (json[0].active > lineData.max)
				lineData.max = json[0].active;
			lineData.datasets[0].data.push(json[0].active);
			
			lineData.labels.length == 0 || lineData.labels[lineData.labels.length - 1] == "-" ? lineData.labels.push("+") : lineData.labels.push("-");
			lineData.labels.push();
			if (lineData.datasets[0].data.length > 5) {
				lineData.datasets[0].data.splice(0, 1);
				lineData.labels.splice(0,1);
			}
			
			var ctx = document.getElementById("specificLine").getContext("2d");
			new Chart(ctx).Line(lineData, {scaleOverride : true, scaleSteps : 10,	scaleStepWidth : parseInt(lineData.max / 10 + 1), scaleStartValue : 0, animation: false});
		}
	});
	

}

function drawLabels (data, id) {
	var ctx = document.getElementById(id).getContext("2d");
	ctx.clearRect(0, 0, 200, 200);
	var i = 1;
	$.each (data, function (key, value) {
		ctx.fillStyle = value.color;
		ctx.fillRect(20, 8 * 2 * i, 10, 10);
		ctx.font = "12px Lucida Console";
		ctx.fillStyle = "#000";
		ctx.fillText(value.label, 35, 8 * 2 * i + 9);
		ctx.font = "10px Arial";
		ctx.fillStyle = "#999";
		ctx.fillText("(" + value.value + ")", 35 + value.label.length * 8, 8 * 2 * i + 8);
		i++;
	});
}

function showBarStatistics (interval) {
	if (barCounter == interval) return;
	
	$("#barsInfo").fadeOut("fast");
	
	$($("#barsButtons li").get(barCounter)).find('img').attr("src", "img/home/slide_off.png");
	
	barCounter = interval;
	
	$($("#barsButtons li").get(barCounter)).find('img').attr("src", "img/home/slide_on.png");

	barDataShow.labels = [];
	barDataShow.datasets[0].data = [];
	barDataShow.datasets[1].data = [];
	$("#barsInfo ul").empty();
	var max = 0;
	for (var i = 5 * interval; i < barData.labels.length && i < 5 * (interval + 1); i++) {
		barDataShow.labels.push(barData.labels[i]);
		barDataShow.datasets[0].data.push(barData.data1[i]);
		barDataShow.datasets[1].data.push(barData.data2[i]);
		
		if (barData.data1[i] > max) max = barData.data1[i];
		if (barData.data2[i] > max) max = barData.data2[i];
		
		// Update graph bar help
		$("#barsInfo ul").append("<li>" + barData.labels[i] + " - " + barData.courses[i] + "</li>");
		
		
	}
	
	var img = new Image();
	img.src = "img/help.png";
	
	var ctx = document.getElementById("specificBars").getContext("2d");
	new Chart(ctx).Bar(barDataShow, {scaleOverride : true, scaleSteps : 10,	scaleStepWidth : parseInt(max/10 + 1), scaleStartValue : 0});
	
	
	setTimeout(function(){
	
		ctx.drawImage(img, 470, 20)
		
		if (clickHelp == null)
			clickHelp =	$('#specificBars').click(function(e) {
					var x = e.offsetX,
						y = e.offsetY;
						
					if (x > 470 && x < 486 && y > 20 && y < 36) {
						if ($("#barsInfo").css("display") == "none") {
							$("#barsInfo").fadeIn("slow");
							$("#barsInfo").css({top: e.pageY + 20 + "px", left: e.pageX + "px"});
						} else
							$("#barsInfo").fadeOut("slow");
					}
					
				});
	
	;},1177);
	
	
}

function showStatistics (uni) {
	if (actualUni == uni) return;
	
	actualUni = uni;
	
	var dataSex = [];
	var POSTdata = {func: 1, uni: uni};
	barDataShow.labels = [];
	barDataShow.datasets[0].data = [];
	barDataShow.datasets[1].data = [];
	barData.labels = [];
	barData.data1 = [];
	barData.data2 = [];
	
	$("#loadingUniversity").fadeIn("slow");
	
	$.ajax({
		type: "POST",
		url: "server/retrieveStatistics.php", 
		contentType: "application/x-www-form-urlencoded;charset=UTF-8",
        dataType: 'json',
		data: POSTdata,
		success: function(json) {
			var max = 0;
			var totalM = 0;
			var totalF = 0;
			$.each( json, function( key, value ) {
				$.each( value, function( key, value ) {
					if (key == "Acronym")
						barData.labels.push(value);
					if (key == "Course")
						barData.courses.push(value);
					else if (key == "M") {
						barData.data1.push(parseInt(value));
						totalM += parseInt(value);
					} else if (key == "F") {
						barData.data2.push(parseInt(value));
						totalF += parseInt(value);
					}
					if (parseInt(value) > max)
						max = parseInt(value);
				});
			});
			
			// Show bars
			barCounter = -1;
			showBarStatistics(0);
			var j = 0;
			$("#barsButtons").empty();
			for (var i = 0; i < barData.labels.length; i += 5) {
				if (i == 0)
					$("#barsButtons").append('<li onclick="showBarStatistics(' + j + ')"><img src="img/home/slide_on.png" /></li>');
				else
					$("#barsButtons").append('<li onclick="showBarStatistics(' + j + ')"><img src="img/home/slide_off.png" /></li>');
				j++;
			}
			
			addHover("#barsButtons li");
			
			// Show Pie
			dataSex.push({label: "Homens", value: totalM, color: colorsSex[0]});
			dataSex.push({label: "Mulheres", value: totalF, color: colorsSex[1]});
			
			
			var ctx = document.getElementById("specificSex").getContext("2d");
			new Chart(ctx).Pie(dataSex, options);
			
			drawLabels(dataSex, "specificSexLabels");
			setInterval(function(){$(".graphLabels").fadeIn("slow");},1700);
			
			// Line graph
			lineData.labels = ["-"];
			lineData.datasets[0].data = [0];
			var ctx = document.getElementById("specificLine").getContext("2d");
			new Chart(ctx).Line(lineData, {scaleOverride : true, scaleSteps : 10,	scaleStepWidth : parseInt(lineData.max / 10 + 1), scaleStartValue : 0, animation: false});
			
			updateUsers();
			
			$("#loadingUniversity").fadeOut("slow");
			
		}
	});
		
}

$(function () {
	var dataUni = [];
	var dataSex = [];
	var POSTdata = {func: 0};
	$.ajax({
		type: "POST",
		url: "server/retrieveStatistics.php", 
		contentType: "application/x-www-form-urlencoded;charset=UTF-8",
        dataType: 'json',
		data: POSTdata,
		success: function(json) {
			var i = 0;
			var totalf = 0, totalm = 0;
			$.each( json, function( key, value ) {
				var total = 0;
				$.each( value, function( key, value ) {
					if (key == "M") {
						total += parseInt(value);
						totalm += parseInt(value);
					}
					if (key == "F") {
						total += parseInt(value);
						totalf += parseInt(value);
					}
						
				});
				dataUni.push({label: value.University, value: total, color: colorsRed[i]});
				i++;
			});
			
			dataSex.push({label: "Homens", value: totalm, color: colorsSex[0]});
			dataSex.push({label: "Mulheres", value: totalf, color: colorsSex[1]});
			
			// Ploting pie charts
			var ctx = document.getElementById("generalUni").getContext("2d");
			new Chart(ctx).Pie(dataUni, options);
			
			var ctx = document.getElementById("generalSex").getContext("2d");
			new Chart(ctx).Pie(dataSex, options);
			
			drawLabels(dataUni, "generalUniLabels");
			drawLabels(dataSex, "generalSexLabels");
			setTimeout(function(){$(".graphLabels").fadeIn("slow");},1700);
			
		}
	});
	showStatistics (1);
});