var mongoose = require('mongoose'),
	timestamp = require('mongoose-timestamp'),
	issueModel = require('./issue.js');

var bugSchema = new mongoose.Schema({
	hash: String,
	issues: [issueModel.schema]
});

bugSchema.plugin(timestamp);
module.exports = mongoose.model('bug', bugSchema);