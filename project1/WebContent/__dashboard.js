function handleResult(result) {
	console.log(result);
}

function handleAddStarCallback(data) {
	$("#message").text(data["message"]);
}

function handleAddStar(event) {
	event.preventDefault();
	$.get("api/add-star", $("#add-star-form").serialize(), (data)=>handleAddStarCallback(data));
}

$("#add-star-form").on("submit", (event)=>handleAddStar(event));

//$.ajax({
//	dataType: 'json',
//	method: 'GET',
//	url: 'api/_dashboard',
//	success: (result) => handleResult(result)
//});