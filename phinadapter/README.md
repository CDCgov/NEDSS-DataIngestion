NEDSS-DataIngestion - phinadapter

Intent for this module is to simulate raphsody route trigger by phin system

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
    Ensure jwtgenerator is running to get jwt tokens.

To build locally: 
    docker build -t phinadapter .

To run locally:
    docker-compose -f compose-me.yml up

To test locally:
    curl http://localhost:8090/phinadapter/v1/elrwqactivator -H 'Content-Type: application/json' -H 'APP-TOKEN: eyJhbGciOiJIUzUxMiJ9.eyJuYW1lIjoiRW5xdWl6aXQgSW5jIiwiZW1haWwiOiJuYnNtb2Rlcml6YXRpb25AZW5xdWl6aXQuY29tIiwicGhyYXNlIjoiYWxsIHRoYXQgZ2xpdHRlcnMgaXMgbm90IGdvbGQiLCJzdWIiOiJOQlMgTWVkZXJpemF0aW9uIiwianRpIjoiZDQ5ZjJhNWYtYzg1Zi00NGYxLThkMzMtYTUyYjk2NDVkMjhiIiwiaWF0IjoxNjc2OTEyNTI5LCJleHAiOjE2NzY5MTYxMjl9.eYHImeP15vZucnZSRUKshfekqCN99HB3MHFxswUDGZoWbeZQ05E9LESDKzt0D8n_QGv2bnYH0MFf2f2-V7Kctw' -X POST -d @phin125.json
    (phin125.json can be found in ".../scripts" directory)

-----------------------------------------  Kubernetes in local env  ----------------------------------------------------------

Using kubernetes in local environments:
   Ensure kubectl and minikube tools are installed on the local host.
   Ensure minikube is running on the local host.

   [@TBD: Third party services need to be discussed]


To build locally: 
    docker build -t raddanki64/eqartifacts:phinadapter .

To run locally:
    kubectl apply -f config-me.yml
    kubectl apply -f deploy-me.yml
    kubectl apply -f service-me.yml
    kubectl port-forward service/phinadapter 8090:8090&

To test locally:
    curl http://localhost:8090/phinadapter/v1/elrwqactivator -H 'Content-Type: application/json' -H 'APP-TOKEN: eyJhbGciOiJIUzUxMiJ9.eyJuYW1lIjoiRW5xdWl6aXQgSW5jIiwiZW1haWwiOiJuYnNtb2Rlcml6YXRpb25AZW5xdWl6aXQuY29tIiwicGhyYXNlIjoiYWxsIHRoYXQgZ2xpdHRlcnMgaXMgbm90IGdvbGQiLCJzdWIiOiJOQlMgTWVkZXJpemF0aW9uIiwianRpIjoiZDQ5ZjJhNWYtYzg1Zi00NGYxLThkMzMtYTUyYjk2NDVkMjhiIiwiaWF0IjoxNjc2OTEyNTI5LCJleHAiOjE2NzY5MTYxMjl9.eYHImeP15vZucnZSRUKshfekqCN99HB3MHFxswUDGZoWbeZQ05E9LESDKzt0D8n_QGv2bnYH0MFf2f2-V7Kctw' -X POST -d @phin125.json
    (phin125.json can be found in ".../scripts" directory)

To check logs:
    Get pod id, by doing: 
        kubectl get pod
        (make a note of first column vaue, ex: phinadapter-76c8c47bbc-pmcvx)

    Review logs using:
        kubectl logs phinadapter-76c8c47bbc-pmcvx

