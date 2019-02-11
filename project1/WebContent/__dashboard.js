function handleResult(result) {
	console.log(result);
}

$.ajax({
	dataType: 'json',
	method: 'GET',
	url: 'api/_dashboard',
	success: (result) => handleResult(result)
});