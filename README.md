VGM
===
A simple app for allowing shippers to lookup tare weights of specific containers and to produce certificates of the verified the gross mass.


Dependencies
============

- Java 8
- Maven
- Wildfly 8.2.1
- AWS SES
- SQL-based DB
- NPM
- Bower



Run Wildfly

    NOTE: aws keys required to use AWS SES Services
    ```
    ./bin/standalone.sh -Denv=VGM -Daws.accessKeyId=<key> -Daws.secretKey=<secretKey>
                        # Environments: [VGM]
    ```

Deploy the archive via `mvn wildfly:deploy`




The application will be running at the following URL: <http://localhost:8080/>.
