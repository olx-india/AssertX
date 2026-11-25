// user-service mock. Requires `uuid`, installed through externalServices.dependencies in api-testing.yml.
const { v4: uuidv4 } = require('uuid');

const users = {
  '42': { id: '42', name: 'Ada' },
  '7': { id: '7', name: 'Grace' },
};

exports.getUser = (req, res) => {
  const user = users[req.params.userId];
  if (!user) {
    return res.status(404).send({ error: 'not found' });
  }
  return res.send(Object.assign({ requestId: uuidv4() }, user));
};

exports.createUser = (req, res) => {
  users[req.body.id] = { id: req.body.id, name: req.body.name };
  return res.status(201).send(users[req.body.id]);
};
