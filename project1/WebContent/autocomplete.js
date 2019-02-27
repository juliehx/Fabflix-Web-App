var lookupCache = {};


function handleLookup(query, doneCallback) {
	console.log("start autocomplete")
	
	if(query in lookupCache){
		
		console.log("lookup cache successful")
		console.log(lookupCache[query])
		doneCallback({suggestions:lookupCache[query]});
	}
	else{
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
}

function handleLookupAjaxSuccess(data, query, doneCallback) {
	console.log("lookup ajax successful")
	console.log(data)
	
	if(!(query in lookupCache)){
		lookupCache[query] = data;
	}
	//check past query results here
	
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

var time = setInterval(myTimer,180000);

function myTimer(){
	console.log("clearing cache...")
	lookupCache = {};
	console.log("cache cleared!")
	
}