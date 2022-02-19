const COLOR_YELLOW = 'rgba(255, 165, 0, 0.5)'
const COLOR_GREEN = 'rgba(60, 179, 113, 0.5)'
const COLOR_BLUE = 'rgba(0, 0, 255, 0.5)'

// ===================
// # Websocket Connect
// ===================
function getStompClient() {
	var pathUrl = window.location.protocol + '//' + window.location.hostname + ':' + window.location.port + '/' + window.location.pathname.split('/')[1];
	var socket = new SockJS(pathUrl + '/batch-websocket');
	stompClient = Stomp.over(socket);
	return stompClient;
}

// ================
// # Topic Connect
// ================
function connectBatchEnv() {

	var pathTopic = '/topic/environment';
	stompClient = getStompClient();
	stompClient.connect({}, function(frame) {
		console.log('Connected: ' + frame);
		console.log('Subscribe: ' + pathTopic);

		stompClient.subscribe(pathTopic, function(traffic) {
			//console.log(pathTopic + traffic);
			let env = JSON.parse(traffic.body);
			updateBatchEnv(env);
		});
	});
}


// ================
// # Update Page
// ================

function updateBatchEnv(env) {
	$.each($('i[id*="_ICO_ENV"]'), function( _, value ) {
		if (env + '_ICO_ENV' === value.id ) {
			$(jq(value.id)).prop('class', 'fa fa-toggle-on')
		} else {
			$(jq(value.id)).prop('class', 'fa fa-toggle-off')
		}
	});
}


//https://learn.jquery.com/using-jquery-core/faq/how-do-i-select-an-element-by-an-id-that-has-characters-used-in-css-notation/
function jq(myid) {
	return "#" + myid.replace(/(:|\.|\[|\]|,|=|@)/g, "\\$1");
}