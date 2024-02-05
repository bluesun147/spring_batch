docker run -itd --name docker_batch_app \
  --network docker_batch_network \
  # application_docker.properties의 내용 바탕으로 운영
  -e SPRING_PROFILES_ACTIVE=docker \
  # 리눅스 상의 위치를 매핑시킴
  -v /APP/spring_batch/INFILES:/INFILES \
  docker_batch_app_image