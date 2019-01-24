function handleLogin(result) {
	resultDataJson = JSON.parse(result);
	console.log(resultDataJson);
	
	if(resultDataJson["status"] == "success") {
		window.location.replace("index.html");
	}
	else {
		console.log(resultDataJson["message"]);
		var messageHtml = '<input class="form-control" type="text" placeholder="'+ resultDataJson["message"] + 'readonly>'
		$("#login_form").append(messageHtml);
	}
}

function handleSubmitForm(event) {
	event.preventDefault();
	
	$.post("api/login", $("#login_form").serialize(), (result) => handleLogin(result));
	
	$("#login_form")[0].reset();
}

$("#login_form").on("submit", (event) => handleSubmitForm(event));