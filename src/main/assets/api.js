var SCC = {
	jobId: "",

	//master api

	_lastKnownWorkerId: -1,

	_lastKnownMessageId: -1,

	_newWorkerCallback: null,

	_newMessageCallback: null,

	_updateWorkersList: function() {
		$.get('/job/' + SCC.jobId + '/workersList', function(id) {
			if (SCC._newWorkerCallback) {
				for (var i = SCC._lastKnownWorkerId + 1; i < id; i++)
					SCC._newWorkerCallback(i);
				SCC._lastKnownWorkerId = id - 1;
			}
		});
	},

	_updateMessageList: function() {
		$.get('/job/' + SCC.jobId + '/messageList', { fromId: SCC._lastKnownMessageId }, function(msgs) {
			if (SCC._newMessageCallback) {
				msgs.forEach(function(msg) { SCC._newMessageCallback(msg) });
				SCC._lastKnownMessageId += msgs.length;
			}
		});
	},

	setNewWorkerCallback: function(callback) {
		SCC._newWorkerCallback = callback;
	},

	setNewMessageCallback: function(callback) {
		SCC._newMessageCallback = callback;
	},

	sendMessage: function(workerId, message) {
		$.post('/job/' + SCC.jobId + '/worker/' + workerId + '/message', { 'msg': message });
	},

	updateResult: function(result) {
		$('#result')[0].innerHTML = result;
	},

	//worker api

	_newWorkerMessageCallback: null,

	_lastKnownWorkerMessageId: -1,

	workerSelfId: -1,

	workerJs: "",

	_heartBeat: function() {
		$.get('/job/' + SCC.jobId + '/worker/' + SCC.workerSelfId + '/heartBeat');
	},

	_updateWorkerMessageList: function() {
		$.get('/job/' + SCC.jobId + '/worker/' + SCC.workerSelfId + '/messageList', { fromId: SCC._lastKnownWorkerMessageId }, function(msgs) {
			if (SCC._newWorkerMessageCallback) {
				msgs.forEach(function(msg) { SCC._newWorkerMessageCallback(msg) });
				SCC._lastKnownWorkerMessageId += msgs.length;
			}
		});
	},

	setNewMessageToWorkerCallback: function(callback) {
		SCC._newWorkerMessageCallback = callback;
	},

	sendMessageToMaster: function(message) {
		$.post('/job/' + SCC.jobId + '/message', { 'msg': message });
	}
};
