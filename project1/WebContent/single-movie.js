function getParameterByName(name) {
	let url = window.location.search;
	let id = url.replace("?id=", "");
	console.log(id);
	return id;
}

function parseMovieInfoHtml(data) {
	return "<tr>" +
//				"<td>" + data["id"] + "</td>" + 
				"<td>" + data["year"] + "</td>" +
				"<td>" + data["director"] + "</td>" +
				"<td>" + data["rating"] + "</td>" +
//				"<td><form id='addCartForm' action='#' method='get'>" +
//        		"		<input type='hidden' name='id' value='" + data['id'] + "'>" +
//        		"		<input type='submit' value='Add to Cart' id='submit-form'>" +
//        		"</form></td>" +
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

function addCartButton(data) {
	return "<input type='hidden' name='id' value='" + data['id'] + "'>" +
			"<input type='submit' value='Add to Cart' class='btn' id='submit-form' data-toggle='popover' data-content='Item is now added to the cart' data-trigger='focus'>";
}

function handleResults(data) {
	let movieTitleElement = jQuery("#movie-title-card");
	let movieInfoElement = jQuery("#movie-info");
	let genreListElement = jQuery("#genre-list");
	let starsListElement = jQuery("#star-list");
	let addCartFormElement = jQuery("#addCartForm");
	
	let movieInfoHtml = parseMovieInfoHtml(data[0]);
	let genresHtml = parseListHtml(data[0]["genres"]);
	let starsHtml = parsestarListHtml(data[0]["stars"]);
	let cartBtnHtml = addCartButton(data[0]);
	
	$("title").append(" | " + data[0]["title"]);
	
	movieTitleElement.append(data[0]["title"]);
	movieInfoElement.append(movieInfoHtml);
	genreListElement.append(genresHtml);
	starsListElement.append(starsHtml);
	addCartFormElement.append(cartBtnHtml);
	
	
	
	document.getElementById("mainResultPage").href = data[0]["url"];
	

}

function handleCartInfo(data) {
	console.log(data);
}

let movieId = getParameterByName("id");

jQuery.ajax({
	datatype: "json",
	method: "GET",
	url: "api/single-movie?id=" + movieId,
	success: (data) => handleResults(data)
});



$(document).on("submit", "#addCartForm", function(event) {
	event.preventDefault();
	console.log($(this).serialize());
	
	$.get('api/add-cart', $(this).serialize(), (data)=>handleCartInfo(data));
	
	$(this).find('#submit-form').popover('show');
	
	return false;
});