var mongoose = require('mongoose'),
	timestamp = require('mongoose-timestamp');

var bugSchema = new mongoose.Schema({
	hash: String,
	issues: [Number]
});

bugSchema.plugin(timestamp);
module.exports = mongoose.model('bug', bugSchema);