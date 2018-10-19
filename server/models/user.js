let mongoose = require('mongoose');
let Schema = mongoose.Schema;
let Step = require('../models/schema/step');

let userSchema = new Schema({
    uid: String,
    name: String,
    steps: [Step],
    coin: String,
    published_date: {type: Date, default: Date.now}
});

module.exports = mongoose.model('user', userSchema);