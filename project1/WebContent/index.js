function handleResult(data) {
    let movieTableElement = jQuery("#movie_table");

    for (let i = 0; i < Math.min(20, data.length); i++) {

        let rowHTML = "";
        rowHTML += "<tr>";
        rowHTML += "<td><a href=\"single-movie.html?id=" + data[i]['id'] + "\">" + data[i]['title'] + "</a></td>";
        rowHTML += "<td>" + data[i]["rating"] + "</td>";
        rowHTML += "</tr>";

        movieTableElement.append(rowHTML);
    }
}

jQuery.ajax({
    dataType: "json",
    method: "GET",
    url: "api/movies",
    success: (data) => handleResult(data)
});