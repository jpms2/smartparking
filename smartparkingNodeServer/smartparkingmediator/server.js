// call the packages that genesis needs
var express    = require('express');
var app        = express();
var bodyParser = require('body-parser');
var mongoose   = require('mongoose');
var request    = require('request');
var HashMap    = require('hashmap');  
var schedule   = require('node-schedule');
var monk = require('monk');
var db = monk('localhost:27017/smartparking');
var collection = db.get('userpreferencescollection');

var userPreference;

mongoose.connect('mongodb://localhost/api', function(err) {
    if(err) {
        console.log('connection error', err);
    } else {
        console.log('connection successful');
    }
});

var userPreferenceSchema = mongoose.Schema({
	userEmail : String,
	visitedBlocks : {blockID : Number,
	 				 timesVisited: Number}
});

var smartparkingPreferences = mongoose.model('SmartParkingPreferences', userPreferenceSchema);

var userPreferencesMap = {};

smartparkingPreferences.find(function (err, userPreferences){
	 if (err) return console.error(err);
     var userPreferenceCollection;
     collection.find({},{},function(e,docs){
          userPreferenceCollection = docs;
	     if( userPreferenceCollection.length > 0)
    	 {
    	 	console.log("Retrieving preferences from database...");
    	 }
    	 else
    	 {
	    	return console.log("No users found in the database");
	    }
    });
});

app.use(bodyParser.urlencoded({ extended : true}));
app.use(bodyParser.json());
app.use(function(req,res,next){
    req.db = db;
    next();
});

var port = process.env.PORT || 8080;

var router = express.Router();

router.get('/get', function(request, response){
    collection.find({},{},function(e,docs){
        var res = docs;
        response.json(res);
    });
});

router.post('/checkForUser', function(req, res) {
    collection.find({},{},function(e,docs){
        var response = {value: false};
        var userPreferenceCollection = docs;
        var userEmail = req.body.userEmail;
        for(var i = 0; i < userPreferenceCollection.length;i++){
                if(userPreferenceCollection[i].userEmail.toUpperCase() === userEmail.toUpperCase()){
                    response = {value: true};
                }
        }
        res.json(response);
    });
});

router.post('/getUserPreferences', function(req, res) {
    collection.find({},{},function(e,docs){
        var userPreferenceCollection = docs;
        var userEmail = req.body.userEmail;
        var user = {};
        var response = {};
        var userDefined = false;
        for(var i = 0; i < userPreferenceCollection.length;i++){
            if(userPreferenceCollection[i].userEmail === userEmail){
                user = userPreferenceCollection[i];
                userDefined = true;
            }
        }
        console.log(userDefined);
        if(!userDefined){
            response = {blockID: 1000};
        }else{
            response = user.visitedBlocks[0];
            for(var j = 1;j < user.visitedBlocks.length;j++){
                if(user.visitedBlocks[i].timesVisited > response.timesVisited){
                    response = "value: " + user.visitedBlocks[i].blockID;
                }
            }
        }
        res.json(response);
    });
});

router.post('/updateUserPreference', function(req, res) {
    collection.find({}, function (err, docs) {
        for (var i = 0; docs[i]; i++) {
            if (docs[i].userEmail === req.body.userEmail) {
                userPreference = docs[i];
            }
        }
        if(userPreference.visitedBlocks.length > 0){
            var gotIn = false;
            for(var i = 0;i < userPreference.visitedBlocks.length;i++){
                if(userPreference.visitedBlocks[i].blockID === req.body.blockID){
                    userPreference.visitedBlocks[i].timesVisited++;
                    gotIn = true;
                }else{
                    if((i + 1) === userPreference.visitedBlocks.length && !gotIn){
                        userPreference.visitedBlocks.push({blockID : req.body.blockID,timesVisited : 0});
                    }
                }
            }
        }else{
            userPreference.visitedBlocks[0].blockID = req.body.blockID;
            userPreference.visitedBlocks[0].timesVisited = 1;
        }
        collection.remove({"userEmail": req.body.userEmail},{"visitedBlocks": {$elemMatch: {"blockID" : req.body.blockID}}}, function(err, user) {
            if (err) throw err;
        });
        collection.insert({
            "userEmail" : userPreference.userEmail,
            "visitedBlocks" : userPreference.visitedBlocks
        }, function (err, doc) {
            if (err) {
                // If it failed, return error
                console.log("There was a problem adding the information to the database.");
            }else{
                res.json(userPreference);
            }
        });
    });
});


router.post('/addUserPreference', function(req, res) {

    // Get our form values. These rely on the "name" attributes
    var userEmail = req.body.userEmail;
    var visitedBlocks = req.body.visitedBlocks;
    var collection = db.get('userpreferencescollection');
    collection.insert({
        "userEmail" : userEmail,
        "visitedBlocks" : visitedBlocks
    }, function (err, doc) {
        if (err) {
            // If it failed, return error
            console.log("There was a problem adding the information to the database.");
        }else{
            res.json("User added with success")
        }
    });
});

app.use('/smartparking', router);

app.listen(port);

console.log('Magic happens on port ' + port);