var mongoose = require('mongoose'),
    bug = require('./models/bug.js'),
    issue = require('./models/issue.js');

function REST_ROUTER(router, md5) {
    var self = this;
    self.handleRoutes(router, md5, self);
}

REST_ROUTER.prototype.handleRoutes= function(router, md5, self) {
    router.get("/",function(req, res){
        res.json({"Message" : "Hello World !"});
    });

    router.post("/submitbug", function(req, res) {
        var issueData = req.body;
        var bugSubmission = submitBug(issueData, md5, function (submission) {

            if (!submission.result) {
                //fixme: Note that passing the exact db error is bad in the real world as this can expose
                //internal implementation to hackers, for the purpose of debugging it has been included
                res.status(500).json({"error" : true, "message" : "Error submitting bug", "dbError": submission.err});
                console.log("Something went wrong: " + submission.err);
            }
            else {
                res.json({"error" : false, "message" : "Bug submitted!"});
            }
        }.bind(this));
    }.bind(self));

    router.get("/bug", function(req, res) {
        var query = bug.find();
        query.populate('issues');
        query.exec(function (err, result) {
            if (err) {
                res.status(500).json({"error" : true, "message" : "error getting bugs", "dbError": bugs.err});
            }
            else {
                res.json({"error" : false, "message" : "All the bugs", "data": result});
            }
        }.bind(this));
    }.bind(self));
}

function submitBug(issueData, md5, callback) {
    var exceptionHash;
    if (issueData.exception === null) {
        exceptionHash = md5("noException");
    }
    else {
        exceptionHash = md5(issueData.exception);
    }

    var newIssue = new issue(issueData);
    newIssue.save(function (err) {
        if (err) callback(handleError(err));
    }.bind(this));

    // check to see if the issue is unique, if it is submit a new bug along with the issue
    // if not then update the bug record with the same hash with the new key.

    bug.findOneAndUpdate({hash: exceptionHash}, {$push: {issues: newIssue}}, {new: true, upsert: true}, function (err) {
        if (err) callback(handleError(err));
    }.bind(this));

    callback({result: true, err: ""});
}

function handleError(err) {
    console.log("Something went wrong: " + err);
    return {result: false, err: "something went wrong"};
}

module.exports = REST_ROUTER;