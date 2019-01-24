function handleSearch(event) {
	event.preventDefault();
	var url = $("#search_form").serialize();
	window.location.replace("movielist.html?"+url);
}

$("#search_form").on("submit", (event) => handleSearch(event));