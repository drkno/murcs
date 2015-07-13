var mongoose = require('mongoose'),
    bug = require('./models/bug.js'),
    issue = require('./models/issue.js');

function REST_ROUTER(router, md5) {
    var self = this;
    self.handleRoutes(router, md5, self);
}

REST_ROUTER.prototype.handleRoutes= function(router, md5, self) {
    router.get("/",function(req, res){
        res.jsonp({
            "Message" : "Hello World below is my api :)",
            "GET requests": {
                "api/bug": "Returns all bugs",
                "api/bug/summary": "Returns all bugs with less data",
                "api/bug/id": "Returns a bug with a given id",
                "api/bug/id/summary": "Returns a bug with a given id and with less data",
                "api/issue": "Returns all issues",
                "api/issue/summary": "Returns all issues with less data",
                "api/issue/id": "Returns a issue with a given id",
                "api/issue/id/summary": "Returns a issue with a given id and with less data"
            },
            "POST requests": {
                "api/submitbug": {
                    "description": "Submits a bug to the web service and expects the following fields",
                    "fields": {
                        "exception": {
                            "description": "An exception thrown by the application, can be null",
                            "type": "String"
                        },
                        "dateTime": {
                            "description": "The date and time of submission",
                            "type": "Date"
                        },
                        "userDescription": {
                            "description": "A description the user reporting the bug can fill out",
                            "type": "String"
                        },
                        "progDescription": {
                            "description": "A description outputted by the program",
                            "type": "String"
                        },
                        "osName": {
                            "description": "The name of the operating system that the application was run on",
                            "type": "String"
                        },
                        "osVersion": {
                            "description": "The version of the operating system that the application was run on",
                            "type": "String"
                        },
                        "args": {
                            "description": "The arguments that the program was running with",
                            "type": "String"
                        },
                        "javaVersion": {
                            "description": "The version of the java that the applicaion was being run on",
                            "type": "String"
                        },
                        "histUndoPossible": {
                            "description": "If undo was possible",
                            "type": "Boolean"
                        },
                        "histRedoPossible": {
                            "description": "If redo was possible",
                            "type": "Boolean"
                        },
                        "navForwardPossible": {
                            "description": "If navigation forward was possible,",
                            "type": "String"
                        },
                        "navBackwardPossible": {
                            "description": "If navigation backwards was possible",
                            "type": "Boolean"
                        },

                        "screenshot": {
                            "description": "An image encoded in base64",
                            "type": "String"
                        },
                        "misc": {
                            "description": "Other data outputted from the application",
                            "type": "String"
                        }
                    }
                }
            }
        });
    });

    router.post("/submitbug", function(req, res) {
        var issueData = req.body;
        var bugSubmission = submitBug(issueData, md5, function (submission) {

            if (!submission.result) {
                //fixme: Note that passing the exact db error is bad in the real world as this can expose
                //internal implementation to hackers, for the purpose of debugging it has been included
                res.status(500).jsonp({"error" : true, "message" : "Error submitting bug", "dbError": submission.err});
                console.log("Something went wrong: " + submission.err);
            }
            else {
                res.jsonp({"error" : false, "message" : "Bug submitted!"});
            }
        }.bind(this));
    }.bind(self));

    router.get("/bug", function(req, res) {
        var query = bug.find();
        query.select('-__v -issues.__v');
        query.exec(function (err, result) {
            if (err) {
                res.status(500).jsonp({"error" : true, "message" : "error getting bugs", "dbError": err});
            }
            else {
                res.jsonp({"error" : false, "message" : "All the bugs", "data": result});
            }
        }.bind(this));
    }.bind(self));

    router.get("/bug/summary", function(req, res) {
        var query = bug.find();
        query.select('hash ' +
        '_id ' +
        'issues._id issues.dateTime ' +
        'issues.progDescription ' +
        'issues.userDescription ' +
        'issues.histRedoPossible ' +
        'issues.histUndoPossible ' +
        'issues.navBackwardPossible ' +
        'issues.navForwardPossible');
        query.exec(function (err, result) {
            if (err) {
                res.status(500).jsonp({"error" : true, "message" : "error getting bugs", "dbError": err});
            }
            else {
                res.jsonp({"error" : false, "message" : "All the bugs", "data": result});
            }
        }.bind(this));
    }.bind(self));

    router.get("/bug/:id", function (req, res) {
        var id = req.params.id;
        if (id.length === 24) {
            var query = bug.findById(id, function (err, result) {
                query.select('-__v -issues.__v');
                if (err) {
                     res.status(500).jsonp({"error" : true, "message" : "error getting bug with id " + req.params.id, "dbError": err});
                }
                else if (result === null) {
                    res.status(404).jsonp({"error" : true, "message" : "Bug with id " + id + " was not found"});
                }
                else {
                    res.jsonp({"error" : false, "message" : "Found bug with id " + id, "data": result});
                }
            }.bind(this));
        }
        else {
            res.status(404).jsonp({"error" : true, "message" : "Invalid id"});
        }
    }.bind(self));

    router.get("/bug/:id/summary", function (req, res) {
        var id = req.params.id;
        if (id.length === 24) {
            var query = bug.findById(id,
                'hash ' +
                '_id ' +
                'issues._id issues.dateTime ' +
                'issues.progDescription ' +
                'issues.userDescription ' +
                'issues.histRedoPossible ' +
                'issues.histUndoPossible ' +
                'issues.navBackwardPossible ' +
                'issues.navForwardPossible', function (err, result) {
                if (err) {
                     res.status(500).jsonp({"error" : true, "message" : "error getting bug with id " + req.params.id, "dbError": err});
                }
                else if (result === null) {
                    res.status(404).jsonp({"error" : true, "message" : "Bug with id " + id + " was not found"});
                }
                else {
                    res.jsonp({"error" : false, "message" : "Found bug with id " + id, "data": result});
                }
            }.bind(this));
        }
        else {
            res.status(404).jsonp({"error" : true, "message" : "Invalid id"});
        }
    }.bind(self));

    router.get("/issue", function (req, res) {
        var query = issue.find();
        query.select('-__v');
        query.exec(function (err, result) {
            if (err) {
                res.status(500).jsonp({"error" : true, "message" : "error getting issues", "dbError": err});
            }
            else {
                res.jsonp({"error" : false, "message" : "All issues", "data": result});
            }
        }.bind(this));
    }.bind(self));

    router.get("/issue/summary", function (req, res) {
        var query = issue.find();
        query.select('_id ' +
        'dateTime ' +
        'progDescription ' +
        'userDescription ' +
        'histRedoPossible ' +
        'histUndoPossible ' +
        'navBackwardPossible ' +
        'navForwardPossible');
        query.exec(function (err, result) {
            if (err) {
                res.status(500).jsonp({"error" : true, "message" : "error getting issues", "dbError": err});
            }
            else {
                res.jsonp({"error" : false, "message" : "All issues", "data": result});
            }
        }.bind(this));
    }.bind(self));

    router.get("/issue/:id", function (req, res) {
        var id = req.params.id;
        if (id.length === 24) {
            var query = issue.findById(id, '-__v', function (err, result) {
                if (err) {
                     res.status(500).jsonp({"error" : true, "message" : "error getting issue with id " + req.params.id, "dbError": err});
                }
                else if (result === null) {
                    res.status(404).jsonp({"error" : true, "message" : "Issue with id " + id + " was not found"});
                }
                else {
                    res.jsonp({"error" : false, "message" : "Found issue with id " + id, "data": result});
                }
            }.bind(this));
        }
        else {
            res.status(404).jsonp({"error" : true, "message" : "Invalid id"});
        }
    }.bind(self));

    router.get("/issue/:id/summary", function (req, res) {
        var id = req.params.id;
        if (id.length === 24) {
            var query = issue.findById(id, '_id ' +
            'dateTime ' +
            'progDescription ' +
            'userDescription ' +
            'histRedoPossible ' +
            'histUndoPossible ' +
            'navBackwardPossible ' +
            'navForwardPossible', function (err, result) {
                if (err) {
                     res.status(500).jsonp({"error" : true, "message" : "error getting issue with id " + req.params.id, "dbError": err});
                }
                else if (result === null) {
                    res.status(404).jsonp({"error" : true, "message" : "Issue with id " + id + " was not found"});
                }
                else {
                    res.jsonp({"error" : false, "message" : "Found issue with id " + id, "data": result});
                }
            }.bind(this));
        }
        else {
            res.status(404).jsonp({"error" : true, "message" : "Invalid id"});
        }
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