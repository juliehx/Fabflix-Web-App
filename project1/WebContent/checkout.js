function handleResult(data) {
	console.log(data);
	$('.message').html("<p>" + data["message"] + "</p>");
	if('status' in data) {
		$('.message').append("<p>Product Details: </p>");
	}
}

function handleSubmitEvent(event) {
	event.preventDefault();
	$.post("api/checkout", $("#checkout-form").serialize(), (data)=>handleResult(data));
}

$("#checkout-form").on("submit", (event)=>handleSubmitEvent(event));