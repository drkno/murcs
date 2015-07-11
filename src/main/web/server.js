var express = require("express");
var bodyParser  = require("body-parser");
var md5 = require('MD5');
var rest = require("./rest.js");
var app  = express();
var mongoose = require("mongoose");

var url = 'mongodb://localhost/sws';

function REST(){
    var self = this;
    self.connectMonodb();
};

REST.prototype.connectMonodb = function() {
    var self = this;
    var db = mongoose.connection;
    db.on('error', console.error.bind(console, 'connection error:'));
    db.once('open', function (callback) {
      console.log("connected to mongodb server.");
      self.configureExpress();
    });
    mongoose.connect(url);
}

REST.prototype.configureExpress = function() {
      var self = this;
      app.use(bodyParser.json());
      app.use(bodyParser.urlencoded({ extended: true }));
      var router = express.Router();
      app.use('/api', router);
      var rest_router = new rest(router, md5);
      self.startServer();
}

REST.prototype.startServer = function() {
      app.listen(3000,function(){
          console.log("All right ! I am alive at Port 3000.");
      });
}

REST.prototype.stop = function(err) {
    console.log("ISSUE WITH MONGODB \n" + err);
    process.exit(1);
}

new REST();