# setup guide
Requirement:
- Docker + Kubernetes Desktop
- Kubectl
- AWS CLI
    - AWS CLI need to be configured with approriate user
- Ekscli
    - AWS admin is needed to run Ekscli create cluster

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
    - EKS Permission
        - Adding permission for our aws account, so we can see and manage eks cluster and its configuration on aws console
        - Execute these following command
            - ```kubectl edit configmap aws-auth -n kube-system```
            - Add userarn ```mapUsers``` in the file as follow (vim)
            - Continue to add ```groups``` belows userarn as follow
                - ```- system: masters```  
            - Example
                ``` 
                mapUsers: |
                 - userarn: arn:aws:iam::XXXXXX:user/randomuser
                   username: randomuser
                   groups:
                   - system:masters
                ```
    - EBS Add-on
        - Once we're able to access EKS cluster on aws console, we can add-on to the cluster
        - Add Amazon EBS CSI Driver, to use EBS  
    - Create IAM policy for EBS CSI Driver
        - Without the correct policy EKS won't have permission to attach itself to EBS volume
        - For testing use this sample policy
            - https://raw.githubusercontent.com/kubernetes-sigs/aws-ebs-csi-driver/v1.0.0/docs/example-iam-policy.json
        - Attach newly created policy to EKS Node instance role
    - Addition note
        - Get and manage existing cluster
            - ```aws eks update-kubeconfig --name <cluster namne> --region <region> ```
        - Delete cluster
            - ```eksctl delete cluster -f eks-config.yaml```


- On terminal, we need to be in ```NEDSS-DataIngestion\environments\kubernetes\k8s\dev\kafka``` directory
- There are several different directories
    - Service
    - Deployment
    - Storage
    - PVC (persistance volume claim)
- Following each step below to deploy EKS with EBS
- Storage
    - ```kubectl apply -f storage```
    - ```kubectl get storageclass```
        - Check the storage class that was just created ```gp-kafka```
- PVC
    - ```kubectl apply -f pvc```
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
- Completed all steps above will create a stateful kafka cluster on EKS (as for now it is good for dev) 
    - Connecting to Kafka
        - ```kubectl get service``` 
            - Look for service-kafka, check External-Ip column
                - Copy the address and add :9092 as port number to connect
                    - <aws-address>.us-west-1.elb.amazonaws.com:9092
- Additional userful command
    - Access kubectl pod directory 
        - ```kubectl get pod```
            - Get pod id
        - ```kubectl exec pod-id -- ls -la /var/lib/kafka/data```
    - Access EBS drive
		- Go to EC2 instance either ssh or connect to it view AWS console
		- ```lsblk -o NAME,FSTYPE,MOUNTPOINT```
			- List mounted disc
			- Cd and access the directory to view its content
