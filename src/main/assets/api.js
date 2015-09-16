var SCC = {
	jobId: "",

	//master api

	_lastKnownWorkerId: -1,

	_newWorkerCallback: null,

	_updateWorkersList: function() {
		$.get('/job/' + SCC.jobId + '/workersList', function(id) {
			if (SCC._newWorkerCallback) {
				for (var i = SCC._lastKnownWorkerId + 1; i <= id; i++)
					SCC._newWorkerCallback(i);
				SCC._lastKnownWorkerId = id;
			}
		});
	},

	_getMessageList: function(callback) {
		$.get('/job/' + SCC.jobId + '/messageList', callback);
	},

	setNewWorkerCallback: function(callback) {
		SCC._newWorkerCallback = callback;
	},

	setNewMessageCallback: function(callback) {
		//todo
	},

	sendMessage: function(workerId, message) {
		$.post('/job/' + SCC.jobId + '/worker/' + workerId + '/message', { 'msg': message });
	},

	updateResult: function(result) {
		$('#result')[0].innerHTML = result;
	},

	//worker api

	workerSelfId: -1,

	workerJs: "",

	_notifyMaster: function() {
		$.get('/job/' + SCC.jobId + '/newWorker', function(data) { SCC.workerSelfId = data })
	},

	_heartBeat: function() {
		$.get('/job/' + SCC.jobId + '/worker/' + SCC.workerSelfId + '/heartBeat');
	},

	_getMessageList: function(callback) {
		$.get('/job/' + SCC.jobId + '/worker/' + SCC.workerSelfId + '/messageList', callback);
	},

	setNewMessageCallback: function(callback) {
		//todo
	},

	sendMessage: function(message) {
		$.post('/job/' + SCC.jobId + '/message', { 'msg': message });
	}
};