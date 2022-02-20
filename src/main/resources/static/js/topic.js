const COLOR_YELLOW = 'rgba(255, 165, 0, 0.5)';
const COLOR_GREEN = 'rgba(60, 179, 113, 0.5)';
const COLOR_BLUE = 'rgba(0, 0, 255, 0.5)';

// ===================
// # Websocket Connect
// ===================
function getStompClient() {
	var pathUrl = window.location.protocol + '//' + window.location.hostname + ':' + window.location.port + '/' + window.location.pathname.split('/')[1];
	var socket = new SockJS(pathUrl + '/batch-websocket');
	stompClient = Stomp.over(socket);
	stompClient.debug = () => {};
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

function connectJobStartEnd() {

	var pathTopic = '/topic/jobStartEnd';
	let stompClient = getStompClient();
	stompClient.connect({}, function(frame) {
		console.log('Connected: ' + frame);
		console.log('Subscribe: ' + pathTopic);

		stompClient.subscribe(pathTopic, function(traffic) {
			//console.log(pathTopic + traffic);
			let json = JSON.parse(traffic.body);
			updateJobStartEnd(json);
		});
	});
}

// ================
// # Update Page
// ================

function updateBatchEnv(env) {
	//console.log('Env: ' + env);
	$.each($('i[id*="_ICO_ENV"]'), function( _, value ) {
		if (env + '_ICO_ENV' === value.id ) {
			$(jq(value.id)).prop('class', 'fa fa-toggle-on');
		} else {
			$(jq(value.id)).prop('class', 'fa fa-toggle-off');
		}
	});
}

function updateTaskExecutor(json) {
	//console.log('Task Executor activeCount: ' + json.activeCount);
	//console.log('Task Executor percent: ' + json.percent);
	
	$(jq('te_cpz')).text(json.corePoolSize);
	$(jq('te_mpz')).text(json.maxPoolSize);
	$(jq('te_pz')).text(json.poolSize);
	$(jq('te_yml')).text(json.ymlAmountThreads);
	$(jq('te_ac')).text(json.activeCount);
	$(jq('pg_ac')).css('width', json.percent + '%');
	$(jq('pg_ac')).html(json.percent + '%');
	$(jq('pg_ac')).prop('class', 'progress-bar ' + json.color);
}

function updateJobStartEnd(json) {
	console.log('JobStartEnd startJob: ' + json.startJob);
	console.log('JobStartEnd jobId: ' + json.jobId);
	console.log('JobStartEnd jobName: ' + json.jobName);
	
	let toAppend = `<tr id="${json.jobId}_start_end">
	<th scope="row">${json.jobId}</th>
	<td colspan="2">${json.jobName}</td>
	<td>${(json.startJob ? "Started" : "Finished")}</td>
	</tr>`
	console.log(toAppend)
	 $(jq('tbody_startEnd')).prepend(toAppend);
	 
	 let toRemove = (json.jobId - 3) + '_start_end';
	 console.log(toRemove)
	 $(jq(toRemove)).remove();
}

//https://learn.jquery.com/using-jquery-core/faq/how-do-i-select-an-element-by-an-id-that-has-characters-used-in-css-notation/
function jq(myid) {
	return "#" + myid.replace(/(:|\.|\[|\]|,|=|@)/g, "\\$1");
}