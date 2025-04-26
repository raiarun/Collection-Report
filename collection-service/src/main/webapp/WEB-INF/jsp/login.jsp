<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<c:set var="contextPath" value="${pageContext.request.contextPath}"/>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <!-- The above 3 meta tags *must* come first in the head; any other head content must come *after* these tags -->
    <meta name="description" content="">
    <meta name="author" content="">

    <title>Log in with your account</title>
    <link rel="stylesheet" href="css/login.css">
</head>
<body>
<div class="container">
	<div id="logo"></div>
    <form method="POST" action="${contextPath}/login" class="Form_Container">
        <h2 class="Form_Title">Please Login</h2>
        <div class="Form_ElementContainer ${error != null ? 'has-error' : ''}">
            <div class="Form_TextMessage">${msg}</div>
            <input name="username" type="text" class="Form_TextField" placeholder="Username" size="22" autofocus="true"/>
			<br>
            <input name="password" type="password" class="Form_TextField" size="22" placeholder="Password"/>
            <span><br>${errorMsg}</span>
			<br>
			<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
            <button class="Form_SubmitButton" type="submit">Log In</button>
        </div>
    </form>
</div>
<script>
document.body.style.background = "#2ecc71";
var logo = document.getElementById("logo");
var image = document.createElement("img");
//image.src = "css/logo.png";
image.src = '${logo}';
image.style.height="12em";
logo.appendChild(image);
logo.style.marginBottom = '5px';
</script>
</body>
</html>
