function handleResult(result) {
	console.log(result);
	let genreListElement = $("#genre-list");
	for(let i = 0; i < result.length; i++) {
		genreListElement.append("<a href='movielist.html?id=" + result[i]["id"] + "&mode=browse&order=rating&limit=10&page=1'><input type='button' class='btn' value='" + result[i]["genre"] + "'></a>");
	}
}

function handleAlphaNum() {
	let alphaListElement = $("#alpha-buttons");
	for(let i = 0; i <= 9; i++) {
		alphaListElement.append("<a href='movielist.html?search=" + i + "&mode=browse&order=rating&limit=10&page=1'>" + "<input type='button' class='btn' value='" + i + "'></a>");
	}
	for(let i = 65; i <= 90; i++) {
		alphaListElement.append("<a href='movielist.html?search=&#" + i + "&mode=browse&order=rating&limit=10&page=1'>" + "<input type='button' class='btn' value='&#" + i + "'></a>");
	}
}

//function handleAdvSearch(event) {
//	event.preventDefault();
//	var url = $("#advSearch-form").serialize();
//	window.location.replace("movielist.html?"+url+"&mode=search&order=rating&limit=10&page=1");
//}
//
//function handleGenSearch(event) {
//	event.preventDefault();
//	var url = $("#searchbar").serialize();
//	window.location.replace("movielist.html?" + url+"&mode=search&order=rating&limit=10&page=1");
//}

$.ajax({
	dataType: 'json',
	method: 'GET',
	url: 'api/index',
	success: (result) => handleResult(result)
});

$(handleAlphaNum());

//$("#advSearch-form").on("submit", (event) => handleAdvSearch(event));
//
//$("#searchbar").on("submit", (event) => handleGenSearch(event));