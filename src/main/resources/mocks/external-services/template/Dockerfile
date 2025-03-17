FROM node:10.14-stretch

RUN mkdir /app
COPY . /app/
COPY package.json /app/
EXPOSE ${DYNAMIC_PORT}
WORKDIR /app
RUN npm install
CMD npm start