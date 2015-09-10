var SCC = {
	workerJs: "",

	jobId: "",

	workerId: -1,

	//master api

	_getWorkersList: function(callback) {
		$.get('/job/' + jobId + '/workersList', callback);
	},

	_getMessageList: function(callback) {
		$.get('/job/' + jobId + '/messageList', callback);
	},

	setNewWorkerCallback: function(callback) {
		//todo
	},

	setNewMessageCallback: function(callback) {
		//todo
	},

	sendMessage: function(workerId, message) {
		$.post('/job/' + jobId + '/worker/' + workerId + '/message', { 'msg': message });
	},

	updateResult: function(result) {
		$('#result')[0].innerHTML = result;
	},

	//worker api

	_notifyMaster: function() {
		$.get('/job/' + jobId + '/newWorker', function(data) { workerId = data })
	},

	_heartBeat: function() {
		$.get('/job/' + jobId + '/worker/' + workerId + '/heartBeat');
	},

	_getMessageList: function(callback) {
		$.get('/job/' + jobId + '/worker/' + workerId + '/messageList', callback);
	},

	setNewMessageCallback: function(callback) {
		//todo
	},

	sendMessage: function(message) {
		$.post('/job/' + jobId + '/message', { 'msg': message });
	}
};