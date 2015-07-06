var mongoose = require('mongoose'),
    bug = require('./models/bug.js'),
    issue = require('./models/issue.js'),
    test = 7;

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
        console.log(issueData);
        res.send(req.body);
        return;
        issue.nextCount(function(err, count) {

            if (err) {
                return handleError(err);
            }
            var exceptionHash;
            if (issueData.exception === null) {
                exceptionHash = md5("noException");
            }
            else {
                exceptionHash = md5(issueData.exception);
            }
            var bugSubmission = submitBug(exceptionHash, count);

            if (!bugSubmission.result) {
                //fixme: Note that passing the exact db error is bad in the real world as this can expose
                //internal implementation to hackers, for the purpose of debugging it has been included
                res.status(500).json({"error" : true, "message" : "Error submitting bug", "dbError": bugSubmission.err});
                console.log("Something went wrong: " + bugSubmission.err);
            }
            else {
                console.log(issueData);
                issue.create({
                    exception: issueData.exception,
                    dateTime: issueData.dateTime,
                    osName: issueData.osName,
                    osVersion: issueData.osVersion,
                    javaVersion: issueData.javaVersion,
                    args: issueData.args,
                    progDescription: issueData.progDescription,
                    userDescription: issueData.userDescription,
                    histRedoPossible: issueData.histRedoPossible,
                    histUndoPossible: issueData.histUndoPossible,
                    navBackwardPossible: issueData.navBackwardPossible,
                    navForwardPossible: issueData.navForwardPossible,
                    screenshot: issueData.screenshot,
                    misc: issueData.misc
                }, function (err, bugData) {
                    if (err) {
                        //fixme: Note that passing the exact db error is bad in the real world as this can expose
                        //internal implementation to hackers, for the purpose of debugging it has been included
                        res.status(500).json({"error" : true, "message" : "Error submitting bug", "dbError": err});
                        console.log("Something went wrong: " + err);
                        // if there is an error at this point the bug document has been updated.
                        // the record needs to be removed.
                        issue.nextCount(function(err, count) {
                            if (err) {
                                return handleError(err);
                            }
                            var exceptionHash;
                            if (issueData.exception === null) {
                                exceptionHash = md5("noException");
                            }
                            else {
                                exceptionHash = md5(issueData.exception);
                            }
                            removeBug(count, exceptionHash);
                        }.bind(this));
                    }
                    else {
                        res.json({"error" : false, "message" : "Bug submitted!"});
                    }
                }.bind(this));
            }
        }.bind(self));
    }.bind(self));

    router.get("/bug", function(req, res) {
        var query = bug.find();
        query.lean();
        query.exec(function (err, result) {
            if (err) {
                res.status(500).json({"error" : true, "message" : "error getting bugs", "dbError": bugs.err});
            }
            else {
                res.json({"error" : false, "message" : "All the bugs", "data": result});
                return;
                //todo wip
                for (var i = 0; i < bugs.message.length; i++) {
                    for (var j = 0; j < bugs.message[i].length; j++) {

                    }
                }
            }
        }.bind(this));
    }.bind(self));
}

function submitBug(exceptionHash, newIssueId) {
    // check to see if the issue is unique, if it is submit a new bug along with the issue
    // if not then update the bug record with the same hash with the new key.
    var query = bug.findOne({'hash': exceptionHash});
    query.select('issues');
    query.lean();
    query.exec(function (err, result) {
        if (err) return handleError(err);
        if (result !== null && result.issues.length >= 1) {
            //update bug record
            console.log(result.issues);
            var updatedIssues = result.issues;
            updatedIssues.push(newIssueId);
            console.log(updatedIssues);
            bug.update({hash: exceptionHash}, {issues: updatedIssues}, function (err) {
                if (err) return handleError(err);
            }.bind(this))
        }
        else {
            var newIssues = [newIssueId];
            // add new record
            bug.create({'hash': exceptionHash, 'issues': newIssues}, function (err) {
                if (err) {
                    return handleError(err);
                }
            }.bind(this));
        }
    }.bind(this));
    return {result: true, err: ""};
}

function removeBug(issueId, exceptionHash) {
    // check to see if the issue is unique, if it is then remove the bug entry
    // if not then update the bug record by removing the issueId from the list
    var query = this.bug.findOne({'hash': exceptionHash});
    query.lean();
    query.select('issues');
    query.exec(function (err, result) {
        if (err) return this.handleError(err);
        if (result === null) {
            //no record found, this should not happen
            return this.handleError("No record to remove");
        }
        if (result.issues.length === 1) {
            //remove record
            this.bug.remove({hash: exceptionHash}, function (err) {
                if (err) return this.handleError(err);
            }.bind(this));
        }
        else if (result.issues.length > 1) {
            var oldIssues = result.issues;
            var index = oldIssues.indexOf(issueId)
            var updatedIssues = oldIssues.splice(index, 1);
            this.bug.update({hash: exceptionHash}, {issues: updatedIssues}, function (err) {
                if (err) return this.handleError(err);
            }.bind(this));
        }
        else {
            // no record found, this should not happen.
        }
    }.bind(this));
}

function handleError(err) {
    console.log("Something went wrong: " + err);
    return {result: false, err: "something went wrong"};
}

module.exports = REST_ROUTER;