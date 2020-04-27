package co.com.bancolombia.templates.config;

import co.com.bancolombia.templates.Constants;

public class JpaConfigTemplate {

    public static String getJpaConfigFileContent(String packageName) {
        packageName = packageName.replaceAll("\\/", "\\.");
        return "package " + packageName + ".config.jpa;\n" +
                "\n" +
                "import " + packageName + "."+ Constants.MODEL +".secret.Secret;\n" +
                "import co.com.bancolombia.commons.secretsmanager.connector.AbstractConnector;\n" +
                "import co.com.bancolombia.commons.secretsmanager.connector.clients.AWSSecretManagerConnector;\n" +
                "import co.com.bancolombia.commons.secretsmanager.exceptions.SecretException;\n" +
                "import co.com.bancolombia.commons.secretsmanager.manager.GenericManager;\n" +
                "import org.apache.commons.dbcp2.BasicDataSource;\n" +
                "import org.springframework.beans.factory.annotation.Value;\n" +
                "import org.springframework.context.annotation.Bean;\n" +
                "import org.springframework.context.annotation.Configuration;\n" +
                "\n" +
                "@Configuration\n" +
                "public class JpaConfig {\n" +
                "    @Value(\"${databaseConfig.secretName}\")\n" +
                "    private String secretName;\n" +
                "\n" +
                "    @Value(\"${databaseConfig.secretRegion}\")\n" +
                "    private String secretRegion;\n" +
                "\n" +
                "    @Value(\"${databaseConfig.driver}\")\n" +
                "    private String dbDriver;\n" +
                "\n" +
                "    @Value(\"${databaseConfig.connectionString}\")\n" +
                "    private String dbConnectionString;\n" +
                "\n" +
                "    @Bean\n" +
                "    public BasicDataSource getDataSource() {\n" +
                "        BasicDataSource dataSource = new BasicDataSource();\n" +
                "        dataSource.setDriverClassName(this.dbDriver);\n" +
                "        Secret model = this.getSecretModel();\n" +
                "        dataSource.setUrl(this.dbConnectionString);\n" +
                "        dataSource.setUsername(model.getUser());\n" +
                "        dataSource.setPassword(model.getPassword());\n" +
                "        return dataSource;\n" +
                "    }\n" +
                "\n" +
                "    private String getDbConnectionString(Secret secretsModel) {\n" +
                "        return String.format(this.dbConnectionString, secretsModel.getHost(),\n" +
                "                secretsModel.getPort(),\n" +
                "                secretsModel.getDbName());\n" +
                "    }\n" +
                "\n" +
                "    private Secret getSecretModel() {\n" +
                "        //SecretManagerUseCase secrets = new SecretManagerUseCase(consumer);\n" +
                "        //SecretsModel model = (SecretsModel) secrets.getSecrets(this.secretRegion, this.secretName);\n" +
                "        return Secret.builder()\n" +
                "                .dbName(\"CleanArchitecture\")\n" +
                "                .host(\"localhost\")\n" +
                "                .password(\"\")\n" +
                "                .port(\"1433\")\n" +
                "                .user(\"sa\")\n" +
                "                .build();\n" +
                "    }\n" +
                "\n" +
                "    private Secret getSecrets() throws CleanArchitectureException {\n" +
                "        try {\n" +
                "            AbstractConnector connector = new AWSSecretManagerConnector(secretRegion);\n" +
                "            GenericManager manager = new GenericManager(connector);\n" +
                "            return manager.getSecretModel(secretName, Secret.class);\n" +
                "        } catch (SecretException se) {\n" +
                "            throw new CleanArchitectureException(\"Unable to consume secrets\", se.getCause());\n" +
                "        }\n" +
                "    }\n" +
                "}";
    }

}
