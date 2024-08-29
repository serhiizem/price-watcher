sudo yum update -y

sudo yum install docker -y
sudo systemctl start docker
sudo systemctl enable docker
sudo usermod -aG docker ec2-user

sudo curl -L https://github.com/docker/compose/releases/latest/download/docker-compose-$(uname -s)-$(uname -m) -o /usr/local/bin/docker-compose
sudo chmod +x /usr/local/bin/docker-compose

wget https://github.com/prometheus/node_exporter/releases/download/v1.0.0/node_exporter-1.0.0.linux-amd64.tar.gz
tar xvfz node_exporter-1.0.0.linux-amd64.tar.gz
cd node_exporter-1.0.0.linux-amd64/
./node_exporter

curl -o docker-compose.yml  https://bitbucket.org/serhiizem/price-watcher/raw/a17e2d96cf14ce841af405af267102712e71825a/docker-compose.yml