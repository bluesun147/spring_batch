# 네트워크 및 db 정의
docker network create docker_batch_network

# application-docker.properties에서 사용한 db name
docker run -d --name docker_batch_db \
  --network docker_batch_network \
  -e MYSQL_DATABASE=batch \
  -e MYSQL_USER=root \
  -e MYSQL_PASSWORD=bluesun \
  -e MYSQL_ROOT_PASSWORD=bluesun \
  # 어떤 db와 볼륨을 연결할 지 (로컬:컨테이너)
  -v /var/lib/mysql:/var/lib/mysql \
  mariadb

echo 'batch-core 라이브러리 검색 이후 배치에 필요한 기본 테이블 정보 세팅'