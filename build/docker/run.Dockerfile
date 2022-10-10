FROM rust:slim-buster
ARG NODE_VERSION=16.15.0
ARG WASM_PACK_VERSION=0.10.2

WORKDIR /src
RUN apt-get update && \
	apt-get install -y git curl perl pkg-config libssl-dev openssl && \
	rm -rf /var/lib/apt/lists/*
RUN curl -o- https://raw.githubusercontent.com/nvm-sh/nvm/v0.39.1/install.sh | bash && \
	\. "$HOME/.nvm/nvm.sh" && nvm install $NODE_VERSION
ENV PATH="$PATH:/root/.nvm/versions/node/v16.15.0/bin/"
RUN	rustup toolchain install stable
RUN	OPENSSL_NO_VENDOR=1 \
	cargo +stable install wasm-pack --version $WASM_PACK_VERSION
RUN	cargo +stable install cargo-watch
ADD . .
RUN chmod +x ./run 
RUN --mount=type=cache,target=/root/.local/share/.enso-ci/cache ./run runtime build