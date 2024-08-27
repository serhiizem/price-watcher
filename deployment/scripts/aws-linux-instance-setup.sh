sudo yum update -y

sudo yum install docker -y
sudo systemctl start docker
sudo systemctl enable docker
sudo usermod -aG docker ec2-user

docker run --rm -d -p 8000:8000 amazon/dynamodb-local -jar DynamoDBLocal.jar -inMemory -sharedDb
aws dynamodb --no-cli-pager create-table --endpoint-url http://localhost:8000 --table-name Subscriptions --attribute-definitions AttributeName=symbol,AttributeType=S --key-schema AttributeName=symbol,KeyType=HASH --billing-mode PAY_PER_REQUEST

wget https://github.com/prometheus/node_exporter/releases/download/v1.0.0/node_exporter-1.0.0.linux-amd64.tar.gz
tar xvfz node_exporter-1.0.0.linux-amd64.tar.gz
cd node_exporter-1.0.0.linux-amd64/
./node_exporter