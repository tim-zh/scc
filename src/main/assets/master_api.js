var SCC = {
	_lastKnownWorkerId: -1,

	_lastKnownMessageId: -1,

	jobId: "",

    _listNewWorkers: function() {
		$.get('/job/' + SCC.jobId + '/workersList', function(id) {
			if (SCC.onWorkerUp) {
				for (var i = SCC._lastKnownWorkerId + 1; i < id; i++)
					SCC.onWorkerUp(i);
				SCC._lastKnownWorkerId = id - 1;
			}
		});
	},

	_receiveMessages: function() {
		$.get('/job/' + SCC.jobId + '/messageList', { fromId: SCC._lastKnownMessageId }, function(msgs) {
			if (SCC.onMessage) {
				msgs.forEach(SCC.onMessage);
				SCC._lastKnownMessageId += msgs.length;
			}
		});
	},

	onWorkerUp: function(id) { console.log('onWorkerUp: ' + id) },

	onMessage: function(msg) { console.log('onMessage: ' + msg) },

	sendMessage: function(workerId, message) {
		$.post('/job/' + SCC.jobId + '/worker/' + workerId + '/message', { 'msg': message });
	},

	updateResult: function(result) {
		$('#result')[0].innerHTML = result;
	}
};
