function handleResult(data){
	console.log(data);
	let cartContentElement = $("#cart-content");
	let htmlElem = "";
	if('message' in data) {
		htmlElem += "<tr><td>" + data["message"] + "</td></tr>";
	} else { 
		for(var key in data) {
			htmlElem += "<tr>"
			htmlElem += "<td><a href='single-movie.html?id=" + key + "'>" + data[key]["title"] +"</a></td>";
			htmlElem += "<td>" + data[key]["quantity"] + "</td>";
			htmlElem += "<td><form id='deleteCartForm' action='#' method='get'>" +
						"<input type='hidden' name='id' value='" + key + "'>" +
						"<input type='submit' class='btn btn-primary' value='Delete'></form></td></tr>";
		}
	}
	
	cartContentElement.append(htmlElem);
	
}

function handleCartInfo(data) {
	console.log(data);
}

jQuery.ajax({
	datatype: "json",
	method: "GET",
	url: "api/view-cart",
	success: (result) => handleResult(result)
});

$(document).on("submit", "#deleteCartForm", function(event) {
	event.preventDefault();
	$(this).closest('tr').remove()
	$.get("api/delete-cart", $(this).serialize(), (data)=>handleCartInfo(data));
	return false;
});

