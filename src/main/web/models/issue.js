var mongoose = require('mongoose');

var issueSchema = new mongoose.Schema({
	exception: String,
	//Date type is time since UNIX epoch
	dateTime: Date,
	osName: String,
	osVersion: String,
	javaVersion: String,
	args: String,
	progDescription: String,
	userDescription: String,
	histRedoPossible: Boolean,
	histUndoPossible: Boolean,
	navBackwardPossible: Boolean,
	navForwardPossible: Boolean,
	screenshot: String,
	misc: String
});

module.exports = mongoose.model('issue', issueSchema);