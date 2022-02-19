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
	let stompClient = getStompClient();
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

function connectTaskExecutor() {

	var pathTopic = '/topic/taskExecutorInfo';
	let stompClient = getStompClient();
	stompClient.connect({}, function(frame) {
		console.log('Connected: ' + frame);
		console.log('Subscribe: ' + pathTopic);

		stompClient.subscribe(pathTopic, function(traffic) {
			//console.log(pathTopic + traffic);
			let json = JSON.parse(traffic.body);
			updateTaskExecutor(json);
		});
	});
}

// ================
// # Update Page
// ================

function updateBatchEnv(env) {
	console.log('Env: ' + env)
	$.each($('i[id*="_ICO_ENV"]'), function( _, value ) {
		if (env + '_ICO_ENV' === value.id ) {
			$(jq(value.id)).prop('class', 'fa fa-toggle-on')
		} else {
			$(jq(value.id)).prop('class', 'fa fa-toggle-off')
		}
	});
}
//{"corePoolSize":10,"maxPoolSize":10,"poolSize":10,"activeCount":4}
function updateTaskExecutor(json) {
	console.log('Task Executor: ' + json.activeCount)
}

//https://learn.jquery.com/using-jquery-core/faq/how-do-i-select-an-element-by-an-id-that-has-characters-used-in-css-notation/
function jq(myid) {
	return "#" + myid.replace(/(:|\.|\[|\]|,|=|@)/g, "\\$1");
}