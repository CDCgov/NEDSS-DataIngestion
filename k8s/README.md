# setup guide
Requirement:
- Docker + Kubernetes Desktop
- Kubectl
- AWS CLI
- Ekscli

Installation:
- N/A

Setup:
- This setup is under assumption that we have all requirements installed on our machine
- EKS cluster creation
    - Review eks-config.yaml before proceed  
        - Create ec2 key pairs on aws console
        - This will be used to access ec2 instance when needed
    - Execute these following command to create EKS cluster
        - ```cd eks```
        - ```eksctl create cluster -f eks-config.yaml```
    - Addition note
        - Get and manage existing cluster
            - ```aws eks update-kubeconfig --name <cluster namne> --region <region> ```
        - Delete cluster
            - ```eksctl delete cluster -f eks-config.yaml```


- On terminal, we need to be in ```NEDSS-DataIngestion\k8s\kafka``` directory
- Service creation
    - Review yaml files in service directory
    - These services are basically a way to expose a running pods on the network
        - kafka and zookeeper expose designated port number via TCP port
        - ingress manages external access to the service in the cluster
    - Execute these commands to create services
        - ```kubectl apply -f https://raw.githubusercontent.com/kubernetes/ingress-nginx/controller-v1.5.1/deploy/static/provider/cloud/deploy.yaml```
        - ```kubectl apply -f service```
            - ingress could fail due to the first command is not completed
            - if it does re-run the second comand
        - ```kubectl get service```
            - used to check all deployed services 
        - ```kubectl delete -f service```
            - delete all services 
- Deployment creation
    - Review yaml files in  deployment directory
    - Deployment consist of image and neccessary enviroment variable
    - Execute these commands to create deployment
        - ```kubectl apply -f deployment```
        - ```kubectl get deployment```
            - used to check all deployed deployments
        - ```kubectl delete deployment```
            - delete all deployment 
- Completed all steps above will create a stateless kafka cluster (only good for dev) on EKS
    - Connecting to Kafka
        - ```kubectl get service``` 
            - Look for service-kafka, check External-Ip column
                - Copy the address and add :9092 as port number to connect
                    - <aws-address>.us-west-1.elb.amazonaws.com:9092
    - For the sake simplicity, these two services can be executed on IntelliJ 