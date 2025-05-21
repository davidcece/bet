## Requirements ##

- JDK 17
- Maven

## Build ##

`mvn clean install`


This will create the  ScratchGame.jar file in the target folder
Copy the config.json file to the target folder

`cp config.json target\`

## Run the application ##

`cd target`

`java -jar ScratchGame.jar --config config.json --betting-amount 100`

Repeat this command as many times as possible to get different outputs.

You can change the amount as well, depending on your risk appetite
