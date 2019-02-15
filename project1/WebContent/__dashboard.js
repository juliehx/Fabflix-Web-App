function parseTableContent(key, data) {
	let tableHtml = $("#" + key);
	for(let i = 0; i < data.length; i++) {
		tableHtml.append("<tr><td>" + data[i]["columnName"] + "</td><td>" + data[i]["dataType"] + "</td></tr>");
	}
}

function handleResult(result) {
	console.log(result);
	for(var key in result) {
		$("#metametameta").append("<table id='" + key + "' class='table'><h4>" + key + "</h4>" +
				"<thead><tr><th>Attribute</th><th>Type</th></tr></thead>");
		parseTableContent(key, result[key]);
		$("#metametameta").append("</table>");
	}
}

function handleAddStarCallback(data) {
	$("#star-message").text(data["message"]);
}

function handleAddStar(event) {
	event.preventDefault();
	$.get("api/add-star", $("#add-star-form").serialize(), (data)=>handleAddStarCallback(data));
}

function handleAddMovieCallback(data) {
	$("#movie-message").text(data["message"]);
}

function handleAddMovie(event) {
	event.preventDefault();
	$.post("api/add-movie", $("#add-movie-form").serialize(), (data)=>handleAddMovieCallback(data));
}

$("#add-star-form").on("submit", (event)=>handleAddStar(event));

$("#add-movie-form").on("submit", (event)=>handleAddMovie(event));

$.ajax({
	dataType: 'json',
	method: 'POST',
	url: 'api/show-metadata',
	success: (result) => handleResult(result)
});