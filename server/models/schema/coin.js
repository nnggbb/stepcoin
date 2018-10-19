let mongoose = require('mongoose');
let Schema = mongoose.Schema;

let coinSchema = new Schema({
    coin: String,
    published_date: {type: Date, default: Date.now}
});

module.exports = coinSchema;