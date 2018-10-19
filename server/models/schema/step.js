let mongoose = require('mongoose');
let Schema = mongoose.Schema;

let stepSchema = new Schema({
    count: String,
    timestamp: {type: Date, default: Date.now}
}, {_id: false});

module.exports = stepSchema;