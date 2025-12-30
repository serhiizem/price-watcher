gradle buildImage
docker load < build/jib-image.tar

gradle publishImageToLocalRegistry
docker tag pricewatcher:1.0.0 serjayzem/pricewatcher:1.0.0
