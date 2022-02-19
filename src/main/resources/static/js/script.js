//==================================
//	# Loading Screen
//==================================
$(window).on('load', function () {
    $(".loading-overlay").fadeOut(600);
});


//==================================
//	# DELETE
//==================================
function sendDelete(url) {
    var xhttp = new XMLHttpRequest();
    xhttp.open("DELETE", url, true);
    xhttp.onload = function () {
        //let responseURL = xhttp.responseURL;
        //alert(responseURL);
        //window.location.replace(responseURL);
    };
    xhttp.send();
}

//==================================
//	# SUBMIT
//==================================
function submitForm(idForm) {
		alert('aa')
		$("#" + idForm).submit(function(event){
	    event.preventDefault(); // Prevent default action
	    var post_url = $(this).attr("action"); // Get the form action URL
	    var request_method = $(this).attr("method"); // Get form GET/POST method
	    var form_data = $(this).serialize(); // Encode form elements for submission
	
	    $.ajax({
	        url : post_url,
	        type: request_method,
	        data : form_data
	    });
	});
}

//==================================
//	# SUBMIT
//==================================
function changeBatchs(id, status) {
	
	var pathUrl = window.location.protocol + '//' + window.location.hostname + ':' + window.location.port + '/' + window.location.pathname.split('/')[1];
	pathUrl = pathUrl + '/page/batch/status/' + status + '/id/' + id
	console.log(pathUrl);
	    $.ajax({
	        url : pathUrl,
	        type: 'POST',
	        data : ''
	    }); 
}