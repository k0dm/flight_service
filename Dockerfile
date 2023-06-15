
FROM amazoncorretto:17-alpine-jdk

# Create a directory
WORKDIR /app

# Copy all the files from the current directory to the image
COPY . .

# build the project avoiding tests
RUN ./gradlew clean build -x test

# Expose port 8088
EXPOSE 8088

# Run the jar file
CMD ["java", "-jar", "./build/libs/flight_service-0.0.1-SNAPSHOT.jar"]