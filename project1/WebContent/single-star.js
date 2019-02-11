function getParameterByName(name){
	let url = window.location.search;
	let id = url.replace("?id=","")
	return id;
}

function parseMovieListHtml(arr){
	var listHtml = "";
	for(let i = 0; i < arr.length; i++){
		listHtml += "<tr><td><a href='single-movie.html?id=" + arr[i]["movie_id"] + "'>" + arr[i]["movie_name"] + "</a></td></tr>";
	}
	return listHtml;
}

function handleResults(data){
	let starNameElement = jQuery("#star-name-card");
	let stardobElement = jQuery("#star-dob");
	let movieListElement = jQuery("#movie-list");
	
	starNameElement.append(data[0]["name"]);
	stardobElement.append("<tr><td>" + data[0]["birthYear"] + "</td></tr>");
	movieListElement.append(parseMovieListHtml(data[0]["movies"]));
	
	$("title").append(" | " + data[0]["name"]);
	
	document.getElementById("mainResultPage").href = data[0]["url"];

}


let starId = getParameterByName("id");

jQuery.ajax({
	datatype: "json",
	method: "GET",
	url: "api/single-star?id=" + starId,
	success: (data) => handleResults(data)
});