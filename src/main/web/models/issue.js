var mongoose = require('mongoose'),
	autoIncrement = require('mongoose-auto-increment');

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

var connection = mongoose.createConnection("mongodb://localhost/sws");
 
autoIncrement.initialize(connection);

issueSchema.plugin(autoIncrement.plugin, {model: 'issue', field: 'issueId'});
module.exports = mongoose.model('issue', issueSchema);