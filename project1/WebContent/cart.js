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
			
			htmlElem += "<td id='qty'><form id = 'subtractForm' action='#' method='get' class='qty-form'>" +
			"<input type='hidden' name='id' value='" + key + "'>" +
			"<input type='hidden' name='action' value='subtract'>" +
			"<input type='submit' class='btn subtract' value='-'></form><span id='qty-text'>" +data[key]["quantity"];
			
//			htmlElem += "<td id='qty'>" + data[key]["quantity"] + "</td>";
			
			htmlElem += "</span><form id = 'addForm' action='#' method='get' class='qty-form'>" +
						"<input type='hidden' name='id' value='" + key + "'>" +
						"<input type='hidden' name='action' value='add'>" + 
						"<input type='submit' class='btn add' value='+'></form></td>";
			
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
	var qty = $(this).parent().find("span");
	var newQty = parseInt(qty.text()) - 1;
	if(newQty <= 0) {
		$(this).parent().parent().remove();
	}
	$.get("api/adjust-cart", $(this).serialize(), (data)=> handleCartInfo(data));
	qty.text(newQty);
	return false;
});

$(document).on("submit","#addForm",function(event){
	event.preventDefault();
	var qty = $(this).parent().find("span");
	$.get("api/adjust-cart", $(this).serialize(), (data)=> handleCartInfo(data));
	var newQty = parseInt(qty.text()) + 1;
	qty.text(newQty);
	return false;
});

$(document).on("submit", "#deleteCartForm", function(event) {
	event.preventDefault();
	$(this).closest('tr').remove()
	$.get("api/delete-cart", $(this).serialize(), (data)=>handleCartInfo(data));
	return false;
});

