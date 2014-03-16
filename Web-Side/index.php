<?php
	include "header.php";
?>
			
			<div id="fb-root"></div>
			<script>(function(d, s, id) {
			  var js, fjs = d.getElementsByTagName(s)[0];
			  if (d.getElementById(id)) return;
			  js = d.createElement(s); js.id = id;
			  js.src = "//connect.facebook.net/en_US/all.js#xfbml=1&appId=395956050472783";
			  fjs.parentNode.insertBefore(js, fjs);
			}(document, 'script', 'facebook-jssdk'));</script>
			
			
			<section id="contentWrapper">
				<img id="slidetip1" src="img/home/slidetip1.png" />
				<img id="slidetip2" src="img/home/slidetip2.png" />
				<div id="slide">
					<div id="downloadContent">
						<span>Baixar</span>
						<a id="downloadImg" href="https://play.google.com/store/apps/details?id=br.com.unichat.activities" target="_blank"><img src="img/home/download.png" /> </a>
						<?php include "server/retrieveActiveUsers.php" ?>
					</div>
					<div id="slideshow">
						<ul>
							<li>
								<div class="slideInfo">
									<div class="slideTitle">
										<a href="register.php">Registre-se</a>
									</div>
									<div class="slideText">
										Crie sua conta no UniChat para começar a usar. É de graça!
									</div>
								</div>
								<img class="slideImg" src="img/home/slideregister.png" />
							</li>
							<li>
								<div class="slideInfo">
									<div class="slideTitle">
										Valide sua conta
									</div>
									<div class="slideText">
										Acesse seu e-mail cadastrado para validar a sua conta
									</div>
								</div>
								<img class="slideImg" src="img/home/slidemail.png" />
							</li>
							<li>
								<div class="slideInfo">
									<div class="slideTitle">
										<a href="https://play.google.com/store/apps/details?id=br.com.unichat.activities" target="_blank">Baixe o UniChat</a>
									</div>
									<div class="slideText">
										Baixe o UniChat no seu smartphone através da Google Playstore
									</div>
								</div>
								<img class="slideImg" src="img/home/slidedownload.png" />
							</li>
							<li>
								<div class="slideInfo">
									<div class="slideTitle">
										Converse
									</div>
									<div class="slideText">
										Converse com anônimos e faça novos amigos!
									</div>
								</div>
								<img class="slideImg" src="img/home/slideuse.png" />
							</li>
						</ul>
					</div>
					<ul id="slidebuttons">
						<li onclick="chooseSlide(0);"><img src="img/home/slide_off.png" /></li>
						<li onclick="chooseSlide(1);"><img src="img/home/slide_off.png" /></li>
						<li onclick="chooseSlide(2);"><img src="img/home/slide_off.png" /></li>
						<li onclick="chooseSlide(3);"><img src="img/home/slide_off.png" /></li>
					</ul>
					
				</div>
				<div id="content">
					<div id="faq">
						<div class="faqItem">
							<h1> O que é? </h1>
							<p> O UniChat é um aplicativo mobile lançado (até agora) somente para Android. O UniChat é um ambiente de bate-papo que permite com que você converse com uma pessoa 
							da sua universidade anonimamente e aleatoriamente. Suas conversas não são salvas. É perfeito para conhecer novas pessoas, fazer novas amizades e falar sobre os mais diversos assuntos.
							</p>
						</div>
						<div class="faqItem">
							<h1> Como usar? </h1>
							<p> É fácil. Para começar a usar o UniChat basta criar a sua conta lá em cima, validá-la através do seu e-mail, baixar e instalar o aplicativo e começar a usar. A interface do aplicativo
							é simples e objetiva, com poucos toques você já estará conectado a um estranho anônimo e pronto pra conversar.
							</p>
						</div>
					</div>
					<div id="bottomIndex">
						<div id="facebook">
							<div class="fb-like-box" data-href="https://www.facebook.com/Unichat" data-width="230" data-colorscheme="light" data-show-faces="true" data-header="true" data-stream="false" data-show-border="true"></div>
						</div>
						<!--<div id="parceiros">
							<h4>Parceiros</h4> <br />
							<a class="parceiro" href="http://www.google.com.br"><img src="img/home/parceiros/example.png" /> </a>
							<a class="parceiro" href="http://www.google.com.br"><img src="img/home/parceiros/example.png" /> </a>
							<a class="parceiro" href="http://www.google.com.br"><img src="img/home/parceiros/example.png" /> </a>
							<a class="parceiro" href="http://www.google.com.br"><img src="img/home/parceiros/example.png" /> </a>
						</div>-->
					</div>
				</div>
			</section>
<?php
	include "footer.php"
?>
