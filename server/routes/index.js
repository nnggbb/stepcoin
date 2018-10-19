let moment = require('moment');

module.exports = function (app, User) {

    // 1.1.1 GET api coin
    // header : userId, Content-Type : application/json
    // * response : userInfo
    app.get('/api/coin', function (req, res) {
        userCheck(req, res).then(function (values) {
            res.json(values.user)
        })
    });

    // 1.1.2 POST api coin
    // header : userId, Content-Type : application/json
    // query param : coin
    // * response : none
    app.post('/api/coin', function (req, res) {
        console.log('/api/coin post');
        console.log(req);
        userCheck(req, res).then(function (values) {
            if(req.query.coin != null) {
                values.user.coin = req.query.coin;
            }
            User.update({ uid: req.headers.userid }, { $set: values.user }, function(err, output){
                if(err) res.status(500).json({ error: 'database failure' });
                console.log(output);
                if(!output.n) return res.status(404).json({ error: 'userId not found' });
                res.json( { message: 'coin updated' } );
            })
        })
    });

    // 1.1.2 PUT api coin (same as 1.1.2)
    // header : userId, Content-Type : application/json
    // query param : coin
    // * response : none
    app.put('/api/coin', function (req, res) {
        userCheck(req, res).then(function (values) {
            if(req.body.coin != null) {
                values.user.coin = req.body.coin;
            }
            User.update({ uid: req.headers.userid }, { $set: values.user }, function(err, output){
                if(err) res.status(500).json({ error: 'database failure' });
                console.log(output);
                if(!output.n) return res.status(404).json({ error: 'userId not found' });
                res.json( { message: 'coin updated' } );
            })
        })
    });

    // 1.2.1 GET api steps
    // header : userId, Content-Type : application/json
    // payload : startTo, endTo, unitType : "DAILY/HOURLY/MINUTELY"
    // * response : steps' count list
    app.get('/api/steps', function (req, res) {
        userCheck(req, res).then(function (values) {
            let tempSteps = [];
            let pSteps = values.user.steps;
            let pCount = 0;
            for (let i = 0; i < pSteps.length; i++) {
                pCount += parseInt(pSteps[i].count);
            }
            res.json({count: pCount})
            // User.find(function(err, users){
            //     res.json(users)
            // })
        })
    });

    // 1.2.2 POST api steps
    // header : userId, Content-Type : application/json
    // body : count
    // * response : none
    app.post('/api/steps', function (req, res) {
        userCheck(req, res).then(function (values) {
            let pUser = values.user;
            let pSteps = pUser.steps;
            pSteps.push({count: req.body.count, timestamp: new Date()});
            pUser.save(function (err) {
                if (err) res.status(500).json({error: 'failed to update steps'});
                res.json({message: "update success!"});
            });
        })
    });

    // 1.2.3 PUT api steps (same as 1.2.2)
    // header : userId, Content-Type : application/json
    // body : count
    // * response : none
    app.put('/api/steps', function (req, res) {
        userCheck(req, res).then(function (values) {
            let pUser = values.user;
            let pSteps = pUser.steps;
            pSteps.push({count: req.body.count, timestamp: new Date()});
            pUser.save(function (err) {
                if (err) res.status(500).json({error: 'failed to update steps'});
                res.json({message: "update success!"});
            });
        })
    });

    function userCheck(req, res) {
        return new Promise(function (resolve) {
            let USER_ID = req.headers.userid;
            if (USER_ID != null) {
                new Promise(() => {
                });
                User.findOne({uid: USER_ID}, function (err, user) {
                    if (err) return res.status(500).json({error: err});
                    // if(!user) return res.status(404).json({error: 'user not found'});
                    if (!user) {
                        let pUser = new User();
                        pUser.uid = USER_ID;
                        pUser.name = "no name";
                        pUser.coin = 0;
                        pUser.published_date = new Date();
                        pUser.save(function (err) {
                            if (err) {
                                return res.status(500).json({error: err});
                            } else {
                                User.findOne({uid: USER_ID}, function (err, user) {
                                    resolve({req: req, res: res, user: user})
                                });
                            }
                        });
                    } else {
                        resolve({req: req, res: res, user: user})
                    }
                });
            } else {
                return res.status(500).json({error: 'need to header "userid"'});
            }
        })
    }
};