FROM openjdk:11

ADD ./build/libs/*.jar appBatch.jar

# 셸 파일
# batchRun.sh을 batchRun.sh로 넣겠다는 뜻
ADD ./batchRun.sh batchRun.sh