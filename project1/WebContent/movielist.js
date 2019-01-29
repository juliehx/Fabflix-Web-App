function parseGenreListHtml(arr) {
	htmlElem = "<ul style='list-style-type:none; padding-left:0;'>";
	for(let i = 0; i < arr.length; i++) {
		htmlElem += "<li><a href='movielist.html?id=" + arr[i]["genre_id"] + "&mode=browse&page=1'>" + arr[i]["genre_name"] + "</a></li>";
	}
	htmlElem += "</ul>";
	return htmlElem;
}

function parseStarsListHtml(arr) {
	htmlElem = "<ul style='list-style-type:none; padding-left:0;'>";
	for(let i = 0; i < arr.length; i++) {
		htmlElem += "<li><a href='single-star.html?id="+ arr[i]["star_id"] + "'>" + arr[i]["star_name"] + "</a></li>";
	}
	htmlElem += "</ul>";
	return htmlElem;
}

function handleResult(data) {
	console.log(data);
    let movieTableElement = jQuery("#movie_table");

    for (let i = 0; i < data.length; i++) {

        let rowHTML = "";
        rowHTML += "<tr>";
        rowHTML += "<td><a href=\"single-movie.html?id=" + data[i]['id'] + "\">" + data[i]['title'] + "</a></td>";
        rowHTML += "<td>" + data[i]["year"] + "</td>";
        rowHTML += "<td>" + data[i]["rating"] + "</td>";
        rowHTML += "<td>" + parseGenreListHtml(data[i]["genres"]) + "</td>";
        rowHTML += "<td>" + data[i]["director"] + "</td>";
        rowHTML += "<td>" + parseStarsListHtml(data[i]["stars"]) + "</td>";
        rowHTML += "</tr>";

        movieTableElement.append(rowHTML);
    }
    console.log(data);
}

jQuery.ajax({
    dataType: "json",
    method: "GET",
    url: "api/movies"+ window.location.search,
    success: (data) => handleResult(data)
});