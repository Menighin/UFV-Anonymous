<?php
	include "header.php";
	
	include 'server/Database.class.php';
	$database = new Database();
	
?>
			<script type="text/JavaScript" src="js/Chart.js"></script> 
			<script type="text/JavaScript" src="js/statistics.js"></script> 
			<section id="contentWrapper">
				<div id="contentStatistics" style="text-align: center; display: inline-block; min-width: 500px;">
					<h2> Estátisticas Gerais </h2><br/>
					<div id="generalCharts">
						<div class="pieChart">
							<h3>Usuários por universidade</h3><br />
							<canvas style="display: block;" id="generalUni" width="250" height="250">Somente navegadores que suportam HTML5 conseguem visualizar este conteúdo</canvas>
							<canvas class="graphLabels" style="display: none;" id="generalUniLabels" width="250" >Somente navegadores que suportam HTML5 conseguem visualizar este conteúdo</canvas>
						</div>
						<div class="pieChart">
							<h3>Guerra dos sexos</h3><br />
							<canvas style="display: block;" id="generalSex" width="250" height="250">Somente navegadores que suportam HTML5 conseguem visualizar este conteúdo</canvas>
							<canvas class="graphLabels" style="display: none;" id="generalSexLabels" width="250" >Somente navegadores que suportam HTML5 conseguem visualizar este conteúdo</canvas>
						</div>
					</div>
					<br/><h2> Estátisticas por Universidade </h2><br/>
					
					<?php
						$conn = $database->connect();
						$query = "SELECT * FROM universities";
						try {
							$stmt = $conn->prepare($query);
							$stmt->execute();
						} catch (Exception $e) {
							$conn = null;
							exit(1);
						}
								
						$result = $stmt->fetchAll();
						echo "<br/><ul>";
						foreach ($result as $row) {
							echo '<li class="statisticsButton" onclick="showStatistics(' . $row['id'] . ')">' . $row['acronym'] . "</li>";
						}
						echo "</ul><br/>";
						
						$conn = $database->disconnect();
					?>
					
					<div id="loadingUniversity" style="display:none"><img src="img/loader.gif" width="30" height="30" /></div><br/>
					
					<div id="specificCharts">
						
						<div id="lineGraph">
							<h3> Usuários ativos nos ultimos 5 minutos </h3>
							<canvas style="display: block;" id="specificLine" width="500" height="400">Somente navegadores que suportam HTML5 conseguem visualizar este conteúdo</canvas>
						</div><br /><br />
						<div id="barsGraph">
							<h3> Usuários por curso </h3>
							<div id="barsInfo"><ul></ul></div>
							<canvas style="display: block;" id="specificBars" width="500" height="400">Somente navegadores que suportam HTML5 conseguem visualizar este conteúdo</canvas>
							<ul id="barsButtons"></ul>
						</div><br /><br />
						<div id="SpecificPieChart">
							<h3>Guerra dos sexos</h3><br />
							<canvas id="specificSex" width="300" height="300">Somente navegadores que suportam HTML5 conseguem visualizar este conteúdo</canvas><br/>
							<canvas class="graphLabels" style="display: none;" id="specificSexLabels" width="300" >Somente navegadores que suportam HTML5 conseguem visualizar este conteúdo</canvas>
						</div>
					</div>
				</div>
			</section>
<?php
	include "footer.php"
?>
