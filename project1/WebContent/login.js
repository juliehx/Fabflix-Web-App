function handleLogin(result) {
	resultDataJson = JSON.parse(result);
	console.log(resultDataJson);
	
	if(resultDataJson["status"] == "success") {
		if(resultDataJson["type"] == "user") {
			window.location.replace("index.html");
		} else if (resultDataJson["type"] == "employee") {
			window.location.replace("__dashboard.html");
		}
		
	}
	else {
		console.log(resultDataJson["message"]);
		$(".error-msg").text(resultDataJson["message"]);
	}
}

function handleSubmitForm(event) {
	event.preventDefault();
	
	console.log($("#login_form").serialize());
	
	$.post("api/login", $("#login_form").serialize(), (result) => handleLogin(result));
	
	$("#login_form")[0].reset();
	window.grecaptcha.reset();
}

$("#login_form").on("submit", (event) => handleSubmitForm(event));