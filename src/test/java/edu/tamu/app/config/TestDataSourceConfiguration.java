package edu.tamu.app.config;

import java.sql.SQLException;

import org.h2.tools.Server;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
 
@Configuration
@ComponentScan("edu.tamu.app")
@PropertySource("classpath:/config/application.properties")
public class TestDataSourceConfiguration {

	/*@Autowired
    private Environment environment;*/
	
	EmbeddedDatabase db;
 
    @Bean
    public EmbeddedDatabase dataSource() {
    	db = new EmbeddedDatabaseBuilder().setType(EmbeddedDatabaseType.H2).build();
    	return db;
    }

    
    /*
     *  Connect to this database in the browser at localhost 8082, JDBC URL: jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=false
     */
    @Bean(name = "h2WebServer", initMethod="start", destroyMethod="stop")
    public Server h2WebServer() throws SQLException {
        return Server.createWebServer("-web", "-webAllowOthers", "-webPort", "8082");
    }


    @Bean(initMethod="start", destroyMethod="stop")
    @DependsOn(value = "h2WebServer")
    public Server h2Server() throws SQLException {
        return Server.createTcpServer("-tcp", "-tcpAllowOthers", "-tcpPort", "9092");
    }

}
