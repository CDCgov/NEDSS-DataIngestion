FROM gradle:8-jdk21 AS builder

# Copy required sources
COPY deduplication /usr/src/nbs/deduplication

WORKDIR /usr/src/nbs/deduplication

# Build just the deduplication api and any required library
RUN gradle :buildNeeded -x test --no-daemon

RUN jar xf /usr/src/nbs/deduplication/build/libs/deduplication.jar

RUN jdeps --ignore-missing-deps -q  \
  --recursive  \
  --multi-release 21  \
  --print-module-deps  \
  --class-path 'BOOT-INF/lib/*'  \
  build/libs/deduplication.jar > deps.info

RUN jlink \
  --add-modules $(cat deps.info),jdk.crypto.cryptoki \
  --strip-debug \
  --compress 2 \
  --no-header-files \
  --no-man-pages \
  --output runtime

FROM redhat/ubi9-micro:latest

COPY --from=builder /usr/src/nbs/deduplication/runtime /deployment/runtime

COPY --from=builder /usr/src/nbs/deduplication/build/libs/deduplication.jar /deployment/application/api.jar


ENV PATH /deployment/runtime/bin:$PATH

WORKDIR /deployment/application

CMD ["java", "-jar", "api.jar"]


