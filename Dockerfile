FROM openjdk:15

WORKDIR /app

COPY . /app
#COPY . .

#COPY target/dependency/BOOT-INF/lib /app/lib
#COPY target/dependency/META-INF /app/META-INF
#COPY target/dependency/BOOT-INF/classes /app

ENTRYPOINT ["java","-cp","app:app/lib/*","-Dspring.profiles.active=${ENV}","-Dspring.profiles.active=local","com.deloitte.elrr.ElrrDatasyncApplication"]
