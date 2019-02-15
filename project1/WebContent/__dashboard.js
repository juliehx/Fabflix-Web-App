function handleResult(result) {
	console.log(result);
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

//$.ajax({
//	dataType: 'json',
//	method: 'GET',
//	url: 'api/_dashboard',
//	success: (result) => handleResult(result)
//});