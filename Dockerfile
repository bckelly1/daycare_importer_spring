FROM maven:3.9.6

COPY . /usr/src/daycare/

WORKDIR /usr/src/daycare

RUN mvn clean install
RUN mv /usr/src/daycare/target/*.jar /usr/src/daycare/application.jar

CMD ["java", "-jar", "/usr/src/daycare/application.jar"]
