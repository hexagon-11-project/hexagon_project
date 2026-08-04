<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
String ctx = request.getContextPath();
%>
<!doctype html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width,initial-scale=1">
<title>로그인 | HEXAGON PAY</title>
<link rel="stylesheet" href="<%=ctx%>/assets/css/base/variables.css">
<link rel="stylesheet" href="<%=ctx%>/assets/css/base/reset.css">
<link rel="stylesheet" href="<%=ctx%>/assets/css/base/typography.css">
<link rel="stylesheet" href="<%=ctx%>/assets/css/components/buttons.css">
<link rel="stylesheet" href="<%=ctx%>/assets/css/components/forms.css">
<link rel="stylesheet" href="<%=ctx%>/assets/css/pages/auth.css">
</head>
<body class="auth-page">
	<main class="login-card">
		<div class="login-brand">
			<span class="brand-mark">H</span>HEXAGON PAY
		</div>
		<form class="login-form"
			action="<%=ctx%>/pages/environment/user-info.jsp" method="get">
			<div class="field">
				<label for="loginId">아이디</label><input id="loginId" class="input"
					type="text" value="hm0814@naver.com" autocomplete="username">
			</div>
			<div class="field">
				<label for="loginPw">비밀번호</label><input id="loginPw" class="input"
					type="password" autocomplete="current-password">
			</div>
			<button class="btn btn-primary" type="submit">로그인</button>
		</form>
	</main>
</body>
</html>
