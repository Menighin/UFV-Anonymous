<!DOCTYPE html>
<html>
    <head>
        <title></title>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <script src="http://ajax.googleapis.com/ajax/libs/jquery/1.8.2/jquery.min.js"></script>
        <script type="text/javascript">
            $(document).ready(function(){
                
            });
            function sendPushNotification(){
				
				var message = $("#message").val();
				var regId = $("#regId").val();
				
				alert(message + " $$$$ " + regId);
				
                var data = { message : message, regId: regId };               
                $.ajax({
                    url: "send_message.php",
                    type: 'GET',
                    data: data,
                    beforeSend: function() {

                    },
                    success: function(data, textStatus, xhr) {
						alert ("Success bitches");
                        $('.txt_message').val("");
                    },
                    error: function(xhr, textStatus, errorThrown) {
                         alert("Erro: " + textStatus);
                    }
                });
                return false;
            }
			//APA91bFHzP7BVSkpxQM62cAuvCN79SOooJcTW_nNfE6f8nvrYZKbQJFCJ_sQqeKjJ3tEY88r7o78xYMkNzcPwDmjDfFTqDEdahl9ZEanRa1PfDp7ymEzIwSjy1ishR3fE6SZ3pjQBQXgGnQu7VK8umkWviK-jAQC29G-dvUNuWpaGwAd0uvSMrs
        </script>
        <style type="text/css">
            .container{
                width: 950px;
                margin: 0 auto;
                padding: 0;
            }
            h1{
                font-family: 'Helvetica Neue', Helvetica, Arial, sans-serif;
                font-size: 24px;
                color: #777;
            }
            div.clear{
                clear: both;
            }
            ul.devices{
                margin: 0;
                padding: 0;
            }
            ul.devices li{
                float: left;
                list-style: none;
                border: 1px solid #dedede;
                padding: 10px;
                margin: 0 15px 25px 0;
                border-radius: 3px;
                -webkit-box-shadow: 0 1px 5px rgba(0, 0, 0, 0.35);
                -moz-box-shadow: 0 1px 5px rgba(0, 0, 0, 0.35);
                box-shadow: 0 1px 5px rgba(0, 0, 0, 0.35);
                font-family: 'Helvetica Neue', Helvetica, Arial, sans-serif;
                color: #555;
            }
            ul.devices li label, ul.devices li span{
                font-family: 'Helvetica Neue', Helvetica, Arial, sans-serif;
                font-size: 12px;
                font-style: normal;
                font-variant: normal;
                font-weight: bold;
                color: #393939;
                display: block;
                float: left;
            }
            ul.devices li label{
                height: 25px;
                width: 50px;                
            }
            ul.devices li textarea{
                float: left;
                resize: none;
            }
            ul.devices li .send_btn{
                background: -webkit-gradient(linear, 0% 0%, 0% 100%, from(#0096FF), to(#005DFF));
                background: -webkit-linear-gradient(0% 0%, 0% 100%, from(#0096FF), to(#005DFF));
                background: -moz-linear-gradient(center top, #0096FF, #005DFF);
                background: linear-gradient(#0096FF, #005DFF);
                text-shadow: 0 1px 0 rgba(0, 0, 0, 0.3);
                border-radius: 3px;
                color: #fff;
            }
        </style>
    </head>
    <body>
        <div class="container">
            <h1>No of Devices Registered:
            <hr/>
            <ul class="devices">
				<li>
					<form name="" method="GET" action="send_message.php">
						<div class="send_container">
							<input type="text" name="regId" id="regId" placeholder="Registration ID do destinatário" />
							<textarea rows="3" name="message" id="message" cols="25" class="txt_message" placeholder="Type message here"></textarea>
							<input type="submit" class="send_btn" value="Send"/>
						</div>
					</form>
				</li>
            </ul>
        </div>
    </body>
</html>