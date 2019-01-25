function handleAdvSearch(event) {
	event.preventDefault();
	var url = $("#advSearch-form").serialize();
	window.location.replace("movielist.html?"+url);
}

function handleGenSearch(event) {
	event.preventDefault();
	var url = $("#searchbar").serialize();
	window.location.replace("movielist.html?" + url);
}

$("#advSearch-form").on("submit", (event) => handleAdvSearch(event));

$("#searchbar").on("submit", (event) => handleGenSearch(event));