<?php
	include "header.php";
?>

			<section id="contentWrapper">
				<img id="slidetip1" src="img/home/slidetip1.png" />
				<img id="slidetip2" src="img/home/slidetip2.png" />
				<div id="slide">
					<div id="downloadContent">
						<span>DOWNLOAD</span>
						<a id="downloadPago" href="http://www.google.com.br"><img src="img/home/downloadpagohover.png" /> </a>
						<a id="downloadFree" href="http://www.google.com.br"><img src="img/home/downloadfreehover.png" /></a>
						<?php include "server/retrieveActiveUsers.php" ?>
					</div>
					<div id="slideshow">
						<ul>
							<li style="background: url('img/home/slideregister.png') no-repeat">
								<div class="slideInfo">
									<div class="slideTitle">
										<a href="register.php">Registre-se</a>
									</div>
									<div class="slideText">
										Crie sua conta no UniChat para começar a usar. É de graça!
									</div>
								</div>
							</li>
							<li style="background: url('img/home/slidemail.png') no-repeat">
								<div class="slideInfo">
									<div class="slideTitle">
										Valide sua conta
									</div>
									<div class="slideText">
										Acesse seu e-mail cadastrado para validar a sua conta
									</div>
								</div>
							</li>
							<li style="background: url('img/home/slidedownload.png') no-repeat">
								<div class="slideInfo">
									<div class="slideTitle">
										<a href="www.google.com.br">Baixe o UniChat</a>
									</div>
									<div class="slideText">
										Baixe o UniChat no seu smartphone através da Google Playstore
									</div>
								</div>
							</li>
							<li style="background: url('img/home/slideuse.png') no-repeat">
								<div class="slideInfo">
									<div class="slideTitle">
										Converse
									</div>
									<div class="slideText">
										Converse com anônimos e faça novos amigos!
									</div>
								</div>
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
						<div class="faqItem">
							<h1> Diferenças entre pago e grátis </h1>
							<p> Além do dinheiro, a versão paga possui algumas funcionalidades a mais. Abaixo os beneficios: </p>
							<ul>
								<li>O usuário pago pode escolher o sexo da pessoa anônima com quem quer falar.</li>
								<li>O usuário pago pode escolher o curso da pessoa anônima com quem quer falar.</li>
								<li>O aplicativo pago não possui propagandas chatas na tela </li>
							</ul>
						</div>
					</div>
					<!-- <div id="statistics">
						<canvas id="generalSex" width="400" height="300">Somente navegadores que suportam HTML5 conseguem visualizar este conteúdo<canvas>
					</div> -->
				</div>
			</section>
<?php
	include "footer.php"
?>