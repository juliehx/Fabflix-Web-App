function handleResult(data){
	console.log(data);
	let cartContentElement = $("#cart-content");
	let htmlElem = "";
	for(var key in data) {
		htmlElem += "<tr>"
		htmlElem += "<td><a href='single-movie.html?id=" + key + "'>" + data[key]["title"] +"</a></td>";
		htmlElem += "<td>" + data[key]["quantity"] + "</td>";
		htmlElem += "<td><input type='hidden' name='id' value='" + key + "'>" +
		"		<input type='submit' value='Delete' id='submit-form'>" +
		"</form></td>";
	}
	cartContentElement.append(htmlElem);
	
}

jQuery.ajax({
	datatype: "json",
	method: "GET",
	url: "api/view-cart",
	success: (result) => handleResult(result)
});

