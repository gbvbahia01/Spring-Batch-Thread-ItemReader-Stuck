//==================================
//	# Loading Screen
//==================================
$(window).on('load', function () {
    $(".loading-overlay").fadeOut(600);
});


//==================================
//	# Ajax
//==================================
function changeEnvironment(env) {
	
	var pathUrl = window.location.protocol + '//' + window.location.hostname + ':' + window.location.port + '/' + window.location.pathname.split('/')[1];
	pathUrl = pathUrl + '/page/environment/' + env
	console.log(pathUrl);
	    $.ajax({
	        url : pathUrl,
	        type: 'PUT',
	        data : ''
	    }); 
}

function changeReadMode(mode) {
	
	var pathUrl = window.location.protocol + '//' + window.location.hostname + ':' + window.location.port + '/' + window.location.pathname.split('/')[1];
	pathUrl = pathUrl + '/page/read/' + mode
	console.log(pathUrl);
	    $.ajax({
	        url : pathUrl,
	        type: 'PUT',
	        data : ''
	    }); 
}