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

	var pathTopic = '/topic/batch';
	stompClient = getStompClient();
	stompClient.connect({}, function(frame) {
		console.log('Connected: ' + frame);
		console.log('Subscribe: ' + pathTopic);

		stompClient.subscribe(pathTopic, function(traffic) {
			//console.log(pathTopic + traffic);
			let json = JSON.parse(traffic.body);
			updateBatchEnv(json);
		});
	});
}


// ================
// # Update Page
// ================

function updateBatchEnv(json) {
		//console.log('Json: ' + json);


}


//https://learn.jquery.com/using-jquery-core/faq/how-do-i-select-an-element-by-an-id-that-has-characters-used-in-css-notation/
function jq(myid) {
	return "#" + myid.replace(/(:|\.|\[|\]|,|=|@)/g, "\\$1");
}