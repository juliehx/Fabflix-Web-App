function handleResult(data) {
	console.log(data);
	$('.message').html("<p>" + data["message"] + "</p>");
	if('status' in data) {
		$('.message').append("<p>Product Details: </p><ul></ul>");
		for(let i = 0; i < data["sales"].length; i++) {
			$('.message ul').append("<li>" + data["sales"][i]["sale_id"] + ": " + data["sales"][i]["title"] + "</li>");
		}
	}
}

function handleSubmitEvent(event) {
	event.preventDefault();
	$.post("api/checkout", $("#checkout-form").serialize(), (data)=>handleResult(data));
}

$("#checkout-form").on("submit", (event)=>handleSubmitEvent(event));