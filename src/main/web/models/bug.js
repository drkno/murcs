var mongoose = require('mongoose'),
	issueModel = require('./issue.js');

var bugSchema = new mongoose.Schema({
	hash: String,
	issues: [issueModel.schema]
});

module.exports = mongoose.model('bug', bugSchema);