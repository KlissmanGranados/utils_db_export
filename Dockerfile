FROM maven:3.9.1-amazoncorretto-17-debian

RUN apt update && apt -y install locales wget openssh-client && locale-gen en_US.UTF-8
RUN sed -i '/es_ES.UTF-8/s/^# //g' /etc/locale.gen && locale-gen
ENV LANG es_ES.UTF-8
ENV LANGUAGE es_ES:es
ENV LC_ALL es_ES.UTF-8

WORKDIR /home/kg/app
COPY ./ .

RUN wget https://downloads.mongodb.com/compass/mongodb-mongosh_1.10.4_amd64.deb
RUN dpkg -i mongodb-mongosh_1.10.4_amd64.deb
RUN rm mongodb-mongosh_1.10.4_amd64.deb
RUN mvn clean -DskipTests install
ENTRYPOINT ["./entrypoint.sh"]