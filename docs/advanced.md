# Advanced

## How to add parallelisation (Works with failsafe 2.22.x)
1. Move your stepdefinition classes and Runner class(ITMain) under the same package.
2. Add the following lines in your pom.
    ```
    <plugin>
        <groupId>org.apache.maven.plugins</groupId>
        <artifactId>maven-failsafe-plugin</artifactId>
        <version>2.22.0</version>
        <configuration>
          <skip>${skipIntegration}</skip>
          <argLine>${failsafeArgLine}</argLine>
          <includes>
            <include>com.olx.it.InventoryIT</include>
          </includes>
          <parallel>methods</parallel>
          <threadCount>4</threadCount>
        </configuration>
    </plugin>
    ```

## SOAP API Consumer and Downstream XML Response Support
Execution -  Follow the steps listed below to accomplish the objective.

1. Create an XML files  in src/test/resources/mocks/external-services/abc containing your downstream system (i.e abc)  API XML responses.

   abc.xml  - contains the actual response that you will get by calling the soap API.

   abcCallback.xml - blank XML file to cater the SoapActionCallback.

2. Update the JS file(abc.js) to read the XML as mentioned in the below example
   ```
   var fs = require('fs');

   const abcResponseObject = (fs.readFileSync('./user-specified/abc/abc.xml', 'utf8'));
   
   const abcResponseCallbackObject = (fs.readFileSync('./user-specified/abc/abcCallback.xml', 'utf8'));
   
   exports.abcOutput = (req, res) => {
   return res.end(abcResponseObject);
   };
   
   exports.abcCallbackOutput = (req, res) => {
   return res.end(abcResponseCallbackObject);
   };
   ```
3. Configured SOAP API endpoint as well soap action callback API in the api-testing.yml config file

   **Example** :

   ```yaml
   mainClass: com.olx.inventory.InventoryApplication
   
   application:
     spring:
       profile: integration-test
   
   mocks:
     externalServices:
       userSpecified:
         routes:
           - type: payment
             resourcePath: src/test/resources/mocks/external-services/
             folderName: payment-service
             endpoints:
               - methodType: POST
                 path: /payment/payment.asmx
                 method: paymentOutput
   
               - methodType: POST
                 path: /payment/soapcallbackapi
                 method: paymentCallbackOutput
   ```