FROM openjdk:15
ARG DEPENDENCY=target/dependency
COPY ${DEPENDENCY}/BOOT-INF/lib /app/lib
COPY ${DEPENDENCY}/META-INF /app/META-INF
COPY ${DEPENDENCY}/BOOT-INF/classes /app
ENTRYPOINT ["java","-cp","app:app/lib/*","-Dspring.profiles.active=${ENV}","-Dspring.profiles.active=local","com.deloitte.elrr.elrrconsolidate.ElrrConsolidateApplication"]