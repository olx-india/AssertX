const users = { '42': { id: '42', name: 'Ada' } };

exports.getUser = (req, res) => {
  const user = users[req.params.userId];
  return user ? res.send(user) : res.status(404).send({ error: 'not found' });
};
