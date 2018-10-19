let express     = require('express');
let app         = express();
let bodyParser  = require('body-parser');
let mongoose    = require('mongoose');

app.use(bodyParser.urlencoded({ extended: true }));
app.use(bodyParser.json());

let port = process.env.PORT || 8082;

let User = require('./models/user');

let router = require('./routes')(app, User);

let db = mongoose.connection;
db.on('error', console.error);
db.once('open', function(){
    console.log("Connected to mongod server");
});

mongoose.connect('mongodb://localhost/mongodb_tutorial', {useNewUrlParser: true});

let server = app.listen(port, function(){
    console.log("Express server has started on port " + port)
});