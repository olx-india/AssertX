var express = require('express');
var fs = require('fs');
const bodyParserGraphql = require('body-parser-graphql')
var app = express();
var router = express.Router();

// Code for json body parsing
app.use(express.json());
app.use(bodyParserGraphql.graphql())
app.use(express.urlencoded());

${SERVICE_DEFINITIONS}
${PATH_DEFINITIONS}

app.get("/healthcheck", (req, res) => {
    res.send({ success: true, message: "It is working" });
});

app.listen(${SERVICE_PORT});
module.exports = app;
