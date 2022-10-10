# syntax = docker/dockerfile:1.2

ARG GRAALVM_VERSION="21.3.0"
ARG JAVA_VERSION="11"
ARG SBT_VERSION="1.7.1"
ARG SCALAC_VERSION="2.13.8"
ARG FLATBUFFERS_BASE_IMAGE="neomantra/flatbuffers"
ARG FLATBUFFERS_TAG="clang-v1.12.0"

FROM ${FLATBUFFERS_BASE_IMAGE}:${FLATBUFFERS_TAG} as flatbuffers

# Build stage to create a minimal build environment for enso
FROM sbtscala/scala-sbt:graalvm-ce-${GRAALVM_VERSION}-java${JAVA_VERSION}_${SBT_VERSION}_${SCALAC_VERSION} AS enso-build-env

# Install Flatbuffers from https://github.com/neomantra/docker-flatbuffers
COPY --from=flatbuffers /usr/local/bin/flatc /usr/local/bin/flatc

COPY --from=flatbuffers /usr/local/include/flatbuffers /usr/local/include/flatbuffers
COPY --from=flatbuffers /usr/local/include/flatbuffers/idl.h /usr/local/include/flatbuffers/idl.h
COPY --from=flatbuffers /usr/local/include/flatbuffers/registry.h /usr/local/include/flatbuffers/registry.h
COPY --from=flatbuffers /usr/local/include/flatbuffers/reflection.h /usr/local/include/flatbuffers/reflection.h
COPY --from=flatbuffers /usr/local/include/flatbuffers/flexbuffers.h /usr/local/include/flatbuffers/flexbuffers.h
COPY --from=flatbuffers /usr/local/include/flatbuffers/flatc.h /usr/local/include/flatbuffers/flatc.h
COPY --from=flatbuffers /usr/local/include/flatbuffers/minireflect.h /usr/local/include/flatbuffers/minireflect.h
COPY --from=flatbuffers /usr/local/include/flatbuffers/base.h /usr/local/include/flatbuffers/base.h
COPY --from=flatbuffers /usr/local/include/flatbuffers/grpc.h /usr/local/include/flatbuffers/grpc.h
COPY --from=flatbuffers /usr/local/include/flatbuffers/flatbuffers.h /usr/local/include/flatbuffers/flatbuffers.h
COPY --from=flatbuffers /usr/local/include/flatbuffers/reflection_generated.h /usr/local/include/flatbuffers/reflection_generated.h
COPY --from=flatbuffers /usr/local/include/flatbuffers/hash.h /usr/local/include/flatbuffers/hash.h
COPY --from=flatbuffers /usr/local/include/flatbuffers/stl_emulation.h /usr/local/include/flatbuffers/stl_emulation.h
COPY --from=flatbuffers /usr/local/include/flatbuffers/util.h /usr/local/include/flatbuffers/util.h
COPY --from=flatbuffers /usr/local/include/flatbuffers/code_generators.h /usr/local/include/flatbuffers/code_generators.h

COPY --from=flatbuffers /usr/local/include/flatbuffers/idl_parser.cpp /usr/local/include/flatbuffers/idl_parser.cpp
COPY --from=flatbuffers /usr/local/include/flatbuffers/idl_gen_text.cpp /usr/local/include/flatbuffers/idl_gen_text.cpp

COPY --from=flatbuffers /usr/local/lib/libflatbuffers.a /usr/local/lib/libflatbuffers.a

COPY --from=flatbuffers /usr/local/lib/cmake/flatbuffers/FlatbuffersConfig.cmake /usr/local/lib/cmake/flatbuffers/FlatbuffersConfig.cmake
COPY --from=flatbuffers /usr/local/lib/cmake/flatbuffers/FlatbuffersConfigVersion.cmake /usr/local/lib/cmake/flatbuffers/FlatbuffersConfigVersion.cmake
COPY --from=flatbuffers /usr/local/lib/cmake/flatbuffers/FlatbuffersTargets.cmake /usr/local/lib/cmake/flatbuffers/FlatbuffersTargets.cmake
COPY --from=flatbuffers /usr/local/lib/cmake/flatbuffers/FlatbuffersTargets-release.cmake /usr/local/lib/cmake/flatbuffers/FlatbuffersTargets-release.cmake
COPY --from=flatbuffers /usr/local/lib/cmake/flatbuffers/FlatcTargets.cmake /usr/local/lib/cmake/flatbuffers/FlatcTargets.cmake
COPY --from=flatbuffers /usr/local/lib/cmake/flatbuffers/FlatcTargets-release.cmake /usr/local/lib/cmake/flatbuffers/FlatcTargets-release.cmake

# Install NodeJs 16
# Adapted from https://fedoramagazine.org/building-smaller-container-images/
ARG NODEJS_VERSION="16.15.0"
RUN	curl -o- https://raw.githubusercontent.com/nvm-sh/nvm/v0.39.2/install.sh | bash && \
	source $HOME/.nvm/nvm.sh && \
	nvm install ${NODEJS_VERSION} && \
	nvm use ${NODEJS_VERSION}
ENV PATH "${PATH}:/root/.nvm/versions/node/v${NODEJS_VERSION}/bin"

# Install maven
# Adapted from https://github.com/carlossg/docker-maven/blob/master/openjdk-11/Dockerfile
ARG MAVEN_VERSION="3.8.6"
ARG USER_HOME_DIR="/root"
ARG MAVEN_SHA=f790857f3b1f90ae8d16281f902c689e4f136ebe584aba45e4b1fa66c80cba826d3e0e52fdd04ed44b4c66f6d3fe3584a057c26dfcac544a60b301e6d0f91c26
ARG MAVEN_BASE_URL=https://apache.osuosl.org/maven/maven-3/${MAVEN_VERSION}/binaries

RUN mkdir -p /usr/share/maven /usr/share/maven/ref \
  && curl -fsSL -o /tmp/apache-maven.tar.gz ${MAVEN_BASE_URL}/apache-maven-${MAVEN_VERSION}-bin.tar.gz \
  && echo "${MAVEN_SHA}  /tmp/apache-maven.tar.gz" | sha512sum -c - \
  && tar -xzf /tmp/apache-maven.tar.gz -C /usr/share/maven --strip-components=1 \
  && rm -f /tmp/apache-maven.tar.gz \
  && ln -s /usr/share/maven/bin/mvn /usr/bin/mvn

ENV MAVEN_HOME /usr/share/maven
ENV MAVEN_CONFIG "$USER_HOME_DIR/.m2"

# Install rust
# adapted from https://github.com/readthedocs/readthedocs-docker-images/issues/135
ARG WASM_PACK_VERSION="0.10.2"

RUN curl https://sh.rustup.rs -sSf | sh -s -- -y --default-toolchain stable
ENV PATH="/root/.cargo/bin:$PATH"
RUN	--mount=type=cache,target=/usr/local/cargo/registry \
	--mount=type=cache,target=/home/root/app/target \
	rustup toolchain install stable
RUN	--mount=type=cache,target=/usr/local/cargo/registry \
	--mount=type=cache,target=/home/root/app/target \
	OPENSSL_NO_VENDOR=1 \
	cargo +stable install wasm-pack --version $WASM_PACK_VERSION
RUN	--mount=type=cache,target=/usr/local/cargo/registry \
	--mount=type=cache,target=/home/root/app/target \
	cargo +stable install cargo-watch

# Install git

RUN	microdnf install git go rsync && \
	microdnf clean all

# [ ] If you want to be able to build the Launcher Native Image, you will need a native C compiler for your platform as described in the Native Image Prerequisites. On Linux that will be gcc, on macOS you may need xcode and on Windows you need to configure the Developer Command Prompt for Microsoft Visual C++ for the x64 architecture.

RUN node -v
RUN java --version
RUN sbt --version
RUN mvn -v
RUN flatc --version
RUN cargo --version

# Build enso runtime
FROM enso-build-env AS build-runtime
WORKDIR src
ADD . .
RUN chmod +x ./run 
RUN sbt bootstrap
RUN npm i
RUN --mount=type=cache,target=/usr/local/cargo/registry \
    --mount=type=cache,target=/home/root/app/target \
    ./run runtime build

