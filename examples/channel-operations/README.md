# Reactive programming and Clean architecture example


### Before to run the application:

1. Run the scripts/dockerImages.sh file to start a postgresql database 
2. Connect from dbeaver to the docker database (localhost:5432)
3. Create a database called "channeldb" and a schema called "management"
4. Run the ddl (Scripts/ddl.sql)

### Verify connection properties
You need to verify the connection database properties in infraestructure/driven-adapters/r2postgresql/.../config/PostgreSQLConnectionPool.java

### Run the application:
Execute the application running applications/app-service.../MainApplication.java



