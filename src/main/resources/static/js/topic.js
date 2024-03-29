const COLOR_YELLOW = 'rgba(255, 165, 0, 1.0)';
const COLOR_GREEN = 'rgba(60, 179, 113, 1.0)';
const COLOR_RED = 'rgba(255, 99, 132, 1.0)';

const COLOR_GREEN_EFFECT = 'rgba(60, 179, 113, 0.5)';
const COLOR_BLUE_EFFECT = 'rgba(0, 0, 255, 0.5)';

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
		//console.log('Subscribe: ' + pathTopic);

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

	stompClient.connect({}, function() {
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
	stompClient.connect({}, function() {
		stompClient.subscribe(pathTopic, function(traffic) {
			//console.log(pathTopic + traffic);
			let json = JSON.parse(traffic.body);
			updateJobStartEnd(json);
		});
	});
}

function connectReadMode() {

	var pathTopic = '/topic/readMode';
	let stompClient = getStompClient();
	stompClient.connect({}, function() {
		stompClient.subscribe(pathTopic, function(traffic) {
			//console.log(pathTopic + traffic);
			let mode = JSON.parse(traffic.body);
			updateJReadMode(mode);
		});
	});
}

function connectProcessorCounter() {

	var pathTopic = '/topic/countProcess';
	let stompClient = getStompClient();
	stompClient.connect({}, function() {
		stompClient.subscribe(pathTopic, function(traffic) {
			//console.log(pathTopic + traffic);
			let json = JSON.parse(traffic.body);
			updateCountProcess(json);
		});
	});
}

function connectProcessingTime() {

	var pathTopic = '/topic/processingTime';
	let stompClient = getStompClient();
	stompClient.connect({}, function() {
		stompClient.subscribe(pathTopic, function(traffic) {
			//console.log(pathTopic + traffic);
			let json = JSON.parse(traffic.body);
			updateProcessingTime(json);
		});
	});
}

// ================
// # Update Page
// ================

function updateProcessingTime(json) {
	
	
	var classToSet = 'alert alert-primary';
	let diff = parseInt(json.maxDifference);
	
	if (isNaN(diff)) {
		return;
	}
	
	if (diff < 60) {
		classToSet = 'alert alert-primary';
		
	} else if (diff >= 60 && diff < 120) {
		classToSet = 'alert alert-warning';
		
	} else if (diff < 180) {
		classToSet = 'alert alert-dark';
		
	} else {
		classToSet = 'alert alert-danger';
	}
	$(jq('processingTimeDiv')).prop('class', classToSet);
	$(jq('processingTimeValue')).text(diff);
}

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

function updateJReadMode(mode) {
	//console.log('Env: ' + env);
	$.each($('i[id*="_ICO_READ"]'), function( _, value ) {
		if (mode + '_ICO_READ' === value.id ) {
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
	//console.log('JobStartEnd startJob: ' + json.startJob);
	//console.log('JobStartEnd jobId: ' + json.jobId);
	//console.log('JobStartEnd jobName: ' + json.jobName);
	
	let toAppend = `<tr id="${json.jobId}_start_end">
	<th scope="row">${json.jobId}</th>
	<td colspan="2">${json.jobName}</td>
	<td>${(json.startJob ? "Started" : "Finished")}</td>
	</tr>`
	//console.log(toAppend)
	 $(jq('tbody_startEnd')).prepend(toAppend);
	 let newId = `${json.jobId}_start_end`
	 //console.log(newId)
	 if (json.startJob) {
	 	$(jq(newId)).effect("highlight", { color: COLOR_BLUE_EFFECT }, 1500);
	 }	else {
		$(jq(newId)).effect("highlight", { color: COLOR_GREEN_EFFECT }, 1500);
	}		
	 
	 let toRemove = (json.jobId - 4) + '_start_end';
	 //console.log(toRemove)
	 $(jq(toRemove)).remove();
}

function updateCountProcess(json) {
	//console.log('JobStartEnd waiting: ' + json.waiting);
	//console.log('JobStartEnd processing: ' + json.processing);
	//console.log('JobStartEnd finished: ' + json.finished);
	
	const data = {
		labels: [
			'Waiting',
			'Processing',
			'Finished'
		],
		datasets: [{
			label: 'My First Dataset',
			data: [json.waiting, json.processing, json.finished],
			backgroundColor: [
				COLOR_RED,
				COLOR_YELLOW,
				COLOR_GREEN
			],
			hoverOffset: 4
		}]
	};

	const config = {
		type: 'pie',
		data: data,
		options: {
        	animation: false
    	}
	};
	
	let chartStatus = Chart.getChart("pieProcess");
	if (chartStatus != undefined) {
		chartStatus.destroy();
	}
	
	new Chart(document.getElementById('pieProcess'), config );
}

//https://learn.jquery.com/using-jquery-core/faq/how-do-i-select-an-element-by-an-id-that-has-characters-used-in-css-notation/
function jq(myid) {
	return "#" + myid.replace(/(:|\.|\[|\]|,|=|@)/g, "\\$1");
}