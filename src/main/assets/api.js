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
				SCC._lastKnownWorkerId = id;
			}
		});
	},

	_updateMessageList: function() {
		$.get('/job/' + SCC.jobId + '/messageList', { fromId: SCC._lastKnownMessageId }, function(msgs) {
			if (SCC._newMessageCallback) {
				for (var i = SCC._lastKnownMessageId + 1; i < msgs.length; i++)
					SCC._newMessageCallback(msgs[i]);
				SCC._lastKnownMessageId = msgs.length - 1;
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

	workerSelfId: -1,

	workerJs: "",

	_notifyMaster: function(callback) {
		$.get('/job/' + SCC.jobId + '/newWorker', function(data) {
			SCC.workerSelfId = data;
			alert(SCC.workerSelfId);
			callback();
		})
	},

	_heartBeat: function() {
		$.get('/job/' + SCC.jobId + '/worker/' + SCC.workerSelfId + '/heartBeat');
	},

	_updateWorkerMessageList: function() {
		$.get('/job/' + SCC.jobId + '/worker/' + SCC.workerSelfId + '/messageList', function(msg) {
			//todo
		});
	},

	setNewMessageToWorkerCallback: function(callback) {
		_newWorkerMessageCallback = callback;
	},

	sendMessageToMaster: function(message) {
		$.post('/job/' + SCC.jobId + '/message', { 'msg': message });
	}
};