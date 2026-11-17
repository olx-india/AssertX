// Express handler used by AssertX to mock the downstream user-service.
const users = {
  '42': { id: '42', name: 'Ada' },
  '7': { id: '7', name: 'Grace' },
};

exports.getUser = (req, res) => {
  const user = users[req.params.userId];
  if (!user) {
    return res.status(404).send({ error: 'not found' });
  }
  return res.send(user);
};
