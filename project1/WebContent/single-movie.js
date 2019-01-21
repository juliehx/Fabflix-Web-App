function getParameterByName(name) {
	let url = window.location.search;
	let id = url.replace("?id=", "");
	console.log(id);
	return id;
}

function parseMovieInfoHtml(data) {
	return "<tr>" +
				"<td>" + data["id"] + "</td>" + 
				"<td>" + data["year"] + "</td>" +
				"<td>" + data["director"] + "</td>" +
				"<td>" + data["rating"] + "</td>" +
			"</tr>";
}

function parseListHtml(arr) {
	var listHtml = "";
	for(let i = 0; i < arr.length; i++) {
		listHtml += "<tr><td>" + arr[i] + "</td></tr>";
	}
	return listHtml;
}

function parsestarListHtml(arr) {
	var listHtml = "";
	for(let i = 0; i < arr.length; i++) {
		listHtml += "<tr><td><a href='single-star.html?id="+ arr[i]["star_id"] + "'>" + arr[i]["star_name"] + "</a></td></tr>";
	}
	return listHtml;
}

function handleResults(data) {
	let movieTitleElement = jQuery("#movie-title");
	let movieInfoElement = jQuery("#movie-info");
	let genreListElement = jQuery("#genre-list");
	let starsListElement = jQuery("#star-list");
	
	let movieInfoHtml = parseMovieInfoHtml(data[0]);
	let genresHtml = parseListHtml(data[0]["genres"]);
	let starsHtml = parsestarListHtml(data[0]["stars"]);
	
	movieTitleElement.append(data[0]["title"]);
	movieInfoElement.append(movieInfoHtml);
	genreListElement.append(genresHtml);
	starsListElement.append(starsHtml);
}

let movieId = getParameterByName("id");

jQuery.ajax({
	datatype: "json",
	method: "GET",
	url: "api/single-movie?id=" + movieId,
	success: (data) => handleResults(data)
});