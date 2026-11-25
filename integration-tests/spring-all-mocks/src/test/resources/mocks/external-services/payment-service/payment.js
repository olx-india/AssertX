// SOAP payment mock: replies with a canned XML envelope (see docs/advanced.md).
var fs = require('fs');

const paymentResponse = fs.readFileSync('./user-specified/payment-service/payment.xml', 'utf8');

exports.paymentOutput = (req, res) => {
  res.set('Content-Type', 'text/xml');
  return res.end(paymentResponse);
};
