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
			
			htmlElem += "<td><form id = 'subtractForm' action='#' method='get' onSubmit='window.location.reload()'>" +
			"<input type='hidden' name='id' value='" + key + "'>" +
			"<input type='hidden' name='action' value='subtract'>" +
			"<input type='submit' class='btn btn-light' value='-'></form></td>";
			
			htmlElem += "<td>" + data[key]["quantity"] + "</td>";
			
			htmlElem += "<td><form id = 'addForm' action='#' method='get' onSubmit='window.location.reload()'>" +
						"<input type='hidden' name='id' value='" + key + "'>" +
						"<input type='hidden' name='action' value='add'>" + 
						"<input type='submit' class='btn btn-light' value='+'></form></td>";
			
			htmlElem += "<td><form id='deleteCartForm' action='#' method='get'>" +
						"<input type='hidden' name='id' value='" + key + "'>" +
						"<input type='submit' class='btn btn-danger' value='Delete'></form></td></tr>";
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

$(document).on("submit","#subtractForm",function(event){
	event.preventDefault();
	$.get("api/adjust-cart", $(this).serialize(), (data)=> handleCartInfo(data));
	return false;
});

$(document).on("submit","#addForm",function(event){
	event.preventDefault();
	$.get("api/adjust-cart", $(this).serialize(), (data)=> handleCartInfo(data));
	return false;
});

$(document).on("submit", "#deleteCartForm", function(event) {
	event.preventDefault();
	$(this).closest('tr').remove()
	$.get("api/delete-cart", $(this).serialize(), (data)=>handleCartInfo(data));
	return false;
});

