for FILE in *.json;do awslocal dynamodb create-table --cli-input-json file://$FILE; done
