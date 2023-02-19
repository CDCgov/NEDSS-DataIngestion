# NEDSS-DataIngestion - gwtgenerator

Intent for this module is to protected external end points until sso's are finalized.

There are two ways to build, deploy and test in local environment.
    a) Using docker
    b) Using kubernetes

-----------------------------------------  Docker in local env       ----------------------------------------------------------

Using docker in local environments:
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


-----------------------------------------  Kubernetes in local env  ----------------------------------------------------------

Using kubernetes in local environments:
   Ensure kubectl and minikube tools are installed on the local host.
   Ensure minikube is running on the local host.

   [@TBD: Third party services need to be discussed]


To build locally: 
    docker build -t jwtgenerator .

To run locally:
    kubectl apply -f deploy-me.yml
    kubectl apply -f service-me.yml
    kubectl port-forward service/jwtgenerator 8000:8000&

To test locally:
    curl http://localhost:8000/jwt/v1/token -H 'APP-PASSPHRASE: a bird in hand is worth two in the bush' -X GET

To check logs:
    Get pod id, by doing: 
        kubectl get pod
        (make a note of first column vaue, ex: jwtgenerator-59f9b9c9b6-khl76)

    Review logs using:
        kubectl logs jwtgenerator-59f9b9c9b6-khl76

