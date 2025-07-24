## Test container usage

The deduplication-api utilizes Test Containers for some test cases. These tests are executed as part of the Github Action pipeline. In order for these tests to complete successfully, the database container needs to be made available for the Github Action.

Pushing a new container to our ECR can be accomplished by performing the following steps.

- Install [AWS CLI](https://docs.aws.amazon.com/cli/latest/userguide/getting-started-install.html)
- Configure [SSO](https://docs.aws.amazon.com/cli/latest/userguide/cli-configure-sso.html)
- Build the di-mssql image

1. Log in to AWS CLI
   ```
   aws sso login --sso-session <session-name>
   ```
2. Connect docker to AWS ECR (See push commands for more info within the AWS ECR UI)
   ```
   aws ecr get-login-password --region us-east-1 --profile shared | docker login --username AWS --password-stdin 501715613725.dkr.ecr.us-east-1.amazonaws.com
   ```
3. Tag the image
   ```
   docker tag dataingestion-di-mssql:latest 501715613725.dkr.ecr.us-east-1.amazonaws.com/cdc-nbs-modernization/deduplication-test-db:latest
   ```
4. Push the image to ECR
   ```
   docker push 501715613725.dkr.ecr.us-east-1.amazonaws.com/cdc-nbs-modernization/deduplication-test-db:501715613725.dkr.ecr.us-east-1.amazonaws.com/cdc-nbs-modernization/deduplication-test-db:latest
   ```
