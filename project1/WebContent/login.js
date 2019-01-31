function handleLogin(result) {
	resultDataJson = JSON.parse(result);
	console.log(resultDataJson);
	
	if(resultDataJson["status"] == "success") {
		window.location.replace("index.html");
	}
	else {
		console.log(resultDataJson["message"]);
		$(".error-msg").text(resultDataJson["message"]);
	}
}

function handleSubmitForm(event) {
	event.preventDefault();
	
	$.post("api/login", $("#login_form").serialize(), (result) => handleLogin(result));
	
	$("#login_form")[0].reset();
}

$("#login_form").on("submit", (event) => handleSubmitForm(event));