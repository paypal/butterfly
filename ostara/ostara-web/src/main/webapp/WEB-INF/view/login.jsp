<%@ page trimDirectiveWhitespaces="true" contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<!DOCTYPE html>
<html>
<head>
<!-- Don't cache the page to avoid login issues -->
<META HTTP-EQUIV="Pragma" CONTENT="no-cache">
<META HTTP-EQUIV="Expires" CONTENT="-1">

<!-- <link rel="shortcut icon" href="favicon.ico" type="image/vnd.microsoft.icon" /> -->
<link rel="stylesheet" href="//netdna.bootstrapcdn.com/bootstrap/3.0.3/css/bootstrap.min.css">
<!-- Optional theme -->
<link rel="stylesheet" href="//netdna.bootstrapcdn.com/bootstrap/3.0.3/css/bootstrap-theme.min.css">
<title>ostara: platform upgrades as a service</title>
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<link rel="stylesheet" type="text/css" href="resources/css/font-awesome.min.css"/>
<meta name="viewport" content="width=device-width, initial-scale=1.0">

<link rel="stylesheet" type="text/css" href="resources/css/app.css"/>
<link rel="stylesheet" type="text/css" href="resources/css/main.css"/>
</head>

<body>

	<header id="header">
		<div id="logo-group">
			<span id="logo"><img src="resources/img/raptor2-launch1.png" alt="Platform Upgrades as a Service"></span>
			<span id="navtitle" class="navtitle">Platform Upgrades as a Service<sup>Beta</sup></span>
		</div>

		<!-- pulled right: nav area -->

		<!-- end pulled right: nav area -->
	</header>


	<div class="container" style="margin-top: 2em">
		<div>

			<form id="login" role="form" class="form-signin">
				<h4 style="border-bottom: solid 1px #adadad; margin-bottom: 2em; padding-bottom: 8px">Sign in</h4>

				<p>
					<label>User Name (CORP ID enabled for GitHub - <a target="_blank"
						href="http://enotify.corp.ebay.com/general/detail/38877">more info</a>):
					</label> <input type="text" id="username" autofocus="autofocus" required="required" placeholder="User name"
						class="form-control" />
				</p>
				<p>
					<label>CORP Password </label> <input type="password" id="password" required="required" placeholder="Password"
						class="form-control" />
				</p>
				<p>
					<label>One-time GitHub Password </label> <input type="text" id="gitOtp"
						placeholder="One-time GitHub Password (only if 2FA is enabled for your GitHub account)" class="form-control" />
				</p>
				<p>
					<label> <input type="checkbox" value="true" checked="checked" style="padding: 10px" /> <span
						style="padding: 10px">Keep Me Signed In</span></label>
				</p>
				<p>
					<input class="btn btn-lg btn-primary btn-block" type="submit" value="Sign in" />
				</p>

				<p class='auth-error' id="auth-error-message">
					<!-- Placeholder for error messages -->
					<br /> <br />
				</p>
			</form>
		</div>


	</div>

	<div style="position: fixed; width: 100%; height: 30px; padding: 5px; bottom: 0px;">
		<h5 style="margin: 0px">
			<i>Powered by <a href="https://github.com/ebay/Ostara">Ostara</a></i>
		</h5>
	</div>

	<script src="//netdna.bootstrapcdn.com/bootstrap/3.0.3/js/bootstrap.min.js"></script>
	<script src="resources/js/JQuery_1_7.min.js"></script>
	<script>
		(function($win) {
			if ($win.ostara == "undefined") {
				$win.ostara = {};
			}
		}).call(this, window);

		ostara.storage = {
			get : function(key) {

			},
			getFromSession : function(key) {
				var sessionValue = window.sessionStorage.getItem(key);
				if (sessionValue != "undefined") {
					return JSON.parse(sessionValue);
				}
			},
			set : function(key, value, isSession) {
				if (isSession) {
					window.sessionStorage.setItem(key, JSON.stringify(value));
				}
			},
			session : function(key, value) {

			},
			clearSession : function(key, clearAll) {

			},
			clear : function(key, clearAll) {

			}
		};

		$('#login')
				.on(
						"submit",
						function(e) {
							$
									.ajax({
										url : "<c:out value="${OSTARA_SERVICE_URL}"/>" + 'authenticate',
										type : 'post',
										cache : false, // Don't cache the response
										data : {
											"username" : $('#username').val()
										},
										headers : {
											"gitOtp" : $('#gitOtp').val(),
											"password" : $('#password').val()
										}
									})
									.done(
											function(cbResult) {
												if (cbResult.usertoken != "undefined"
														&& cbResult != null) {
													if (cbResult.error != "undefined"
															&& cbResult.error != null) {

														if (cbResult['X-GitHub-OTP'] === "true") {
															message = 'Authentication failed. Please also enter a GitHub One-time password. (<a target="_blank" href="https://help.github.com/enterprise/2.1/user/articles/providing-your-2fa-authentication-code/">help</a>)';
														} else {
															message = 'Authentication failed. Error Message: Incorrect credentials';
														}

														divHeader = '<div class="alert alert-danger" role="alert">';
														divFooter = '</div>';

														document
																.getElementById('auth-error-message').innerHTML = divHeader
																+ message
																+ divFooter;

														$('#login').attr(
																"data-error",
																"unauthorized");
													} else {
														ostara.storage.set(
																"user",
																cbResult, true);
														$('.userName').html(
																"testuser");
														location.href = "index.jsp";
													}

												}
											});

							e.preventDefault();
							return false;
						});
	</script>
</body>
</html>
