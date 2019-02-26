function handleLookup(query, doneCallback) {
	console.log("start autocomplete")
	
	$.ajax({
		datatype: "json",
		method: "GET",
		url: "api/autocomplete-suggestion?query=" + escape(query),
		success: function(data) {
			handleLookupAjaxSuccess(data, query, doneCallback)
		},
		error: function(errorData) {
			console.log("lookup ajax error")
			console.log(errorData)
		}
	})
}

function handleLookupAjaxSuccess(data, query, doneCallback) {
	console.log("lookup ajax successful")
	console.log(data)
	
//	var jsonData = JSON.parse(data);
//	console.log(jsonData)
	
	doneCallback({suggestions:data});
}

function handleSelectSuggestion(suggestion) {
	window.location.replace("single-movie.html?id=" + suggestion["data"]["id"])
}

$("#autocomplete").autocomplete({
	lookup: function(query, doneCallback) {
		handleLookup(query, doneCallback)
	},
	onSelect: function(suggestion) {
		handleSelectSuggestion(suggestion)
	},
	deferRequestBy: 300,
	minChars: 4,
});