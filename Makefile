.phony: launch-dev, clean, debug

clean:
	mvn clean

dev:
	mvn spring-boot:run -D spring-boot.run.profiles=local -e

debug:
	mvn spring-boot:run -Dspring-boot.run.jvmArguments="-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=8000" -D spring-boot.run.profiles=local -e

test:
	mvn test

test-debug:
	mvn -Dmaven.surefire.debug="-agentlib:jdwp=transport=dt_socket,server=y,suspend=y,address=localhost:8000" test


build:
	mvn install
