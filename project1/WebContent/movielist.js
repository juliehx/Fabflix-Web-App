function parseUrl() {
	let url = window.location.search.replace("?", "");
	let parseArgs = url.split("&");
	let paramObj = {};
	for(let i = 0; i < parseArgs.length; i++) {
		let keyArg = parseArgs[i].split("=");
		paramObj[keyArg[0]] = keyArg[1];
	}
	console.log(paramObj);
	return paramObj;
}

function getUrl(paramObj, step, newOrder,newLimit) {
	let newUrl = "?";
	for(var key in paramObj) {
		if(key == "page") {
			newUrl += key + "=" + (parseInt(paramObj[key]) + step) + "&";
		} else if(key == "order") {
			newUrl += key + "=" + newOrder + "&";		
		} else if(key == "limit"){
			newUrl += key + "=" + newLimit + "&";
		} 
		else {
			newUrl += key + "=" + paramObj[key] + "&";
		}
	}
	return newUrl;
}


function handlePagination() {
	let paramObj = parseUrl();
	let pageElem = $("#pag");
	if(paramObj["page"] > "1") {
//		pageElem.append("<li class='page-item' disabled><span class='page-link'>&#8592;</span></li>");
		pageElem.append("<li class='page-item'><a class='page-link' href='movielist.html" + getUrl(paramObj, -1, paramObj["order"],paramObj["limit"]) + "'>&#8592;</a></li>");
	}
//	} else {
//		pageElem.append("<li class='page-item'><a class='page-link' href='movielist.html" + getUrl(paramObj, -1, paramObj["order"],paramObj["limit"]) + "'>&#8592;</a></li>");
//	}
	pageElem.append("<li class='page-item'><a class='page-link' href='movielist.html" + getUrl(paramObj, 1, paramObj["order"],paramObj["limit"]) + "'>&#8594;</a></li>");
}

function parseGenreListHtml(arr) {
//	htmlElem = "<ul style='list-style-type:none; padding-left:0;'>";
	htmlElem = "";
	for(let i = 0; i < arr.length; i++) {
		htmlElem += "<a href='movielist.html?id=" + arr[i]["genre_id"] + "&mode=browse&order=rating&limit=10&page=1'>" + arr[i]["genre_name"] + "</a>&nbsp;";
	}
//	htmlElem += "</ul>";
	return htmlElem;
}

function parseStarsListHtml(arr) {
//	htmlElem = "<ul style='list-style-type:none; padding-left:0;'>";
	htmlElem = "";
	for(let i = 0; i < arr.length; i++) {
		htmlElem += "&nbsp;<a href='single-star.html?id="+ arr[i]["star_id"] + "'>" + arr[i]["star_name"] + "</a>&nbsp;";
	}
//	htmlElem += "</ul>";
	return htmlElem;
}

function handleResult(data) {
	console.log(data);
    let movieTableElement = jQuery("#movie_table");

    for (let i = 0; i < data.length; i++) {
    	
    	let rowHTML = '<div class="movie">\n' +
    				'<img src="images/jakob-owens-231036-unsplash.jpg" alt="movie poster"><h6 class="movie-title">' +
    				'<a href="single-movie.html?id=' + data[i]['id'] + '">' + data[i]['title'] + '</a></h6>' + 
    				'<u>Director</u>: ' + data[i]["director"] + '<br/>'+
    				'<u>Release year</u>: ' + data[i]["year"] + '<br/>' +
    				'<u>Rating</u>: ' + data[i]["rating"] + '<br/><br/>' +
    				parseGenreListHtml(data[i]["genres"]) + "<form id='addCartForm' action='#' method='get' class='cartForm'>" +
    				"<input type='hidden' name='id' value='" + data[i]['id'] + "'>" + 
    				"<button type='submit' class='btn cart-submit' id='submit-form' data-toggle='popover' data-content='Item is now added to the cart' data-trigger='focus'><img src='images/plus.svg'> Add to cart</button>" +
//    				"<input type='submit' class='btn' value='images/plus.svg' id='submit-form'></form></div>";
    				'</div>';

//        let rowHTML = "";
//        rowHTML += "<tr>";
//        rowHTML += "<td><a href=\"single-movie.html?id=" + data[i]['id'] + "\">" + data[i]['title'] + "</a></td>";
//        rowHTML += "<td>" + data[i]["year"] + "</td>";
//        if(data[i]["rating"] == null) {
//        	rowHTML += "<td>" + 0 + "</td>";
//        } else {
//        	rowHTML += "<td>" + data[i]["rating"] + "</td>";
//        }
//        rowHTML += "<td>" + parseGenreListHtml(data[i]["genres"]) + "</td>";
//        rowHTML += "<td>" + data[i]["director"] + "</td>";
//        rowHTML += "<td>" + parseStarsListHtml(data[i]["stars"]) + "</td>";
//        rowHTML += "<td><form id='addCartForm' action='#' method='get'>" +
//        		"		<input type='hidden' name='id' value='" + data[i]['id'] + "'>" +
//        		"		<input type='submit' value='Add to Cart' id='submit-form'>" +
//        		"</form></td>";
//        rowHTML += "</tr>";

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

function handleCartInfo(data) {
	console.log(data);
}

$("#orderFormControl").on("change", function(event) {
	let paramObj = parseUrl();
	window.location.replace("movielist.html" + getUrl(paramObj, 0, $(this).val(),paramObj["limit"]));
});

$("#limitFormControl").on("change", function(event) {
	let paramObj = parseUrl();
	window.location.replace("movielist.html" + getUrl(paramObj,0,paramObj["order"],$(this).val()))
});

$(handlePagination());

function submitCartForm() {
	$.get('api/add-cart', $("#addCartForm").serialize(), (data)=>handleCartInfo(data));
}

$(document).on("submit", "#addCartForm", function(event) {
	event.preventDefault();
	console.log($(this).serialize());
	
	$.get('api/add-cart', $(this).serialize(), (data)=>handleCartInfo(data));
	
	$(this).find('.cart-submit').popover('show');
	
	return false;
});

function handleGenres(result) {
	console.log(result);
	let genreListElement = $("#genreFormControl");
	for(let i = 0; i < result.length; i++) {
		genreListElement.append("<option value='" + result[i]["id"] + "'>" + result[i]["genre"] + "</option>");
	}
}

function handleAlphaNum() {
	let alphaListElement = $("#alphaFormControl");
	for(let i = 0; i <= 9; i++) {
		alphaListElement.append("<option value='" + i + "'>" + i + "</option>");
	}
	for(let i = 65; i <= 90; i++) {
		alphaListElement.append("<option value='&#" + i + "'>&#" + i + "</option>");
	}
}

$.ajax({
	dataType: 'json',
	method: 'GET',
	url: 'api/index',
	success: (result) => handleGenres(result)
});

$(handleAlphaNum());

$("#genreFormControl").on("change", function(event) {
	window.location.replace("movielist.html?id=" + $(this).val() + "&mode=browse&order=rating&limit=10&page=1");
});

$("#alphaFormControl").on("change", function(event) {
	window.location.replace("movielist.html?search=" + $(this).val() + "&mode=browse&order=rating&limit=10&page=1");
});