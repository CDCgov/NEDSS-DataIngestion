# NEDSS-DataIngestion - gwtgenerator

Intent for this module is to protected external end points until sso's are finalized.

Before working with this module:
    Ensure docker and docker-compose tools are installed on the local host.
    Ensure docker is running on the local host.
    Ensure third party services are running under docker container.
    Follow are the known third party services at this point:
        Kafka (along with zookeeper)
        MongoDB
        Hashi vault

To build locally: 
    docker build -t jwtgenerator .

To run locally:
    docker-compose -f compose-me.yml up

To test locally:
    curl http://localhost:8000/jwt/v1/token -H 'APP-PASSPHRASE: a bird in hand is worth two in the bush' -X GET
