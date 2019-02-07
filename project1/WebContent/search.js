function handleAdvSearch(event) {
	event.preventDefault();
	var url = $("#advSearch-form").serialize();
	window.location.replace("movielist.html?"+url+"&mode=search&order=rating&limit=10&page=1");
}

function handleGenSearch(event) {
	event.preventDefault();
	var url = $("#searchbar").serialize();
	window.location.replace("movielist.html?" + url+"&mode=search&order=rating&limit=10&page=1");
}

$("#advSearch-form").on("submit", (event) => handleAdvSearch(event));

$("#searchbar").on("submit", (event) => handleGenSearch(event));