FROM alpine
RUN apk add --update --no-cache lighttpd && rm -rf /var/cache/apk/*
COPY doc /var/www/localhost/htdocs
RUN sed '1 s/^.*$/{/' /var/www/localhost/htdocs/swagger-spec.js > /var/www/localhost/htdocs/api.js
EXPOSE 80
ENTRYPOINT ["lighttpd", "-D", "-f", "/etc/lighttpd/lighttpd.conf"]

ARG BUILD_VERSION
ARG COMMIT
ENV BUILD_VERSION "${BUILD_VERSION}"
ENV COMMIT "${COMMIT}"
LABEL build_version=${BUILD_VERSION}
LABEL commit=${COMMIT}
LABEL maintainer="fabiocarvalho777@gmail.com"