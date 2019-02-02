function handleResult(data){
	console.log(data);
	let cartContentElement = jQuery("#cart-content");
	
	for(let i = 0; i < data.length; i++){
		let movieIdElement = data[i]; //should contain movieID
		let rowHTML = "";
		rowHTML += "<tr>";
		rowHTML += "<td>" + movieIdElement["title"]  + "</td>";
		rowHTML += "<td><input type='number' name= 'quantity' value='" + movieIdElement["quantity"] + "'min='0' max='10'></td>";
		rowHTML += "</tr>";
		
		cartContentElement.append(rowHTML);
	}
	
	
}

jQuery.ajax({
	datatype: "json",
	method: "GET",
	url: "api/cart",
	success: (result) => handleResult(result)
});