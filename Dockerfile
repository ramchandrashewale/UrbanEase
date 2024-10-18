FROM openjdk:17
EXPOSE 8083
ADD target/categoryService.jar categoryService.jar
ENTRYPOINT ["java","-jar","/categoryService.jar"]

#catgories service is name given to avoid confusion with root image