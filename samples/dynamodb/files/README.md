# DynamoDb Mocking


Using AWS dynamodb in your application

## Integration Steps

### Configuration
1. Add `dynamodb` in the services list under localstack in your api-testing.yml
2. For making the warmup files run mention the directory containing JSON files which can be passed to aws-cli dynamodb. Refer to createTable.json for reference
3. Also copy init.sh to the same directory so that it iterates over all the JSON files provided and loads them into dynamodb on startup
	
    - api-testing.yaml -
    
    ```
     localstack:
       enabled: true
       dataDir: src/test/resources/aws/ #path to directory containing startup files
       services:
         - dynamodb #to enable dynamodb service within localstack
       region: us-east-1 #default region
       port: 4566
       image: localstack/localstack
    ```
4. Replace the host (localhost) and port (4566) in your integration-test.yml file for making connection to dynamodb
5. Run your project after making the above changes and verify that dynamodb is running by doing a simple healthcheck.

    ```curl localhost:4566/health```
    
    should return you list of services which are up and running inside your localstack container
    
    
    

