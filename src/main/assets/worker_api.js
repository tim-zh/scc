var SCC = {
	_lastKnownMessageId: -1,

	jobId: "",
    
	workerId: -1,

	_heartBeat: function() {
		$.get('/job/' + SCC.jobId + '/worker/' + SCC.workerId + '/heartBeat');
	},

	_receiveMessages: function() {
		$.get('/job/' + SCC.jobId + '/worker/' + SCC.workerId + '/messageList', { fromId: SCC._lastKnownMessageId }, function(msgs) {
			if (SCC.onMessage) {
				msgs.forEach(SCC.onMessage);
				SCC._lastKnownMessageId += msgs.length;
			}
		});
	},

	onMessage: function(msg) { console.log('onMessage: ' + msg) },

	sendMessageToMaster: function(message) {
		$.post('/job/' + SCC.jobId + '/message', { 'msg': message });
	}
};
