let mongoose = require('mongoose');
let Schema = mongoose.Schema;

let heartRateSchema = new Schema({
    heartRate: String,
    published_date: {type: Date, default: Date.now}
});

module.exports = heartRateSchema;