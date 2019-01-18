function parseGenreListHtml(arr) {
	htmlElem = "<ul style='list-style-type:none; padding-left:0;'>";
	for(let i = 0; i < arr.length; i++) {
		htmlElem += "<li>" + arr[i] + "</li>";
	}
	htmlElem += "</ul>";
	return htmlElem;
}

function parseStarsListHtml(arr) {
	htmlElem = "<ul style='list-style-type:none; padding-left:0;'>";
	for(let i = 0; i < arr.length; i++) {
		htmlElem += "<li><a href='single-star.html'>" + arr[i] + "</a></li>";
	}
	htmlElem += "</ul>";
	return htmlElem;
}

function handleResult(data) {
    let movieTableElement = jQuery("#movie_table");

    for (let i = 0; i < Math.min(20, data.length); i++) {

        let rowHTML = "";
        rowHTML += "<tr>";
        rowHTML += "<td><a href=\"single-movie.html?id=" + data[i]['id'] + "\">" + data[i]['title'] + "</a></td>";
        rowHTML += "<td>" + data[i]["year"] + "</td>";
        rowHTML += "<td>" + data[i]["rating"] + "</td>";
        rowHTML += "<td>" + parseGenreListHtml(data[i]["genres"]) + "</td>";
        rowHTML += "<td>" + parseStarsListHtml(data[i]["stars"]) + "</td>";
        rowHTML += "</tr>";

        movieTableElement.append(rowHTML);
    }
    console.log(data);
}

jQuery.ajax({
    dataType: "json",
    method: "GET",
    url: "api/movies",
    success: (data) => handleResult(data)
});