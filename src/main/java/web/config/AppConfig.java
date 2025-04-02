package web.config;


import web.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.Properties;


@Configuration  //класс содержит определения бинов для контекста Spring.
@PropertySource("classpath:db.properties")  //Spring должен загружать свойства из файла db.properties
@EnableTransactionManagement //Включает поддержку управления транзакциями в Spring. Это позволяет
// использовать аннотации, такие как @Transactional, для управления транзакциями в сервисах.
@ComponentScan(value = "web") //Указывает Spring сканировать пакет hiber на наличие компонентов
// (например, сервисов и DAO), которые будут автоматически зарегистрированы как
// бины в контексте приложения.


public class AppConfig {

    @Autowired  //env будет содержать доступ к свойствам, загруженным из файла db.properties.
    private Environment env;  //interface Environment extends PropertyResolver , а у последнего
    //есть метод getProperty, который используется для получения значения свойства по заданному ключу

    @Bean //DataSource: Обеспечивает соединение с базой данных.
    public DataSource getDataSource() {   //DataSourse это интерфейс
        DriverManagerDataSource dataSource = new DriverManagerDataSource(); //DriverManagerDataSource —
        // это класс из библиотеки Spring, который реализует интерфейс DataSource. Он предоставляет
        // простой способ для получения соединений с базой данных, используя стандартный механизм JDBC
        dataSource.setDriverClassName(env.getProperty("db.driver")); //setDriverClassName это метод DriverManagerDataSource
        dataSource.setUrl(env.getProperty("db.url")); //setUrl метод class AbstractDriverBasedDataSource extends
        // AbstractDataSource, который имплементирует DataSourse
        dataSource.setUsername(env.getProperty("db.username"));
        dataSource.setPassword(env.getProperty("db.password"));
        return dataSource; //настроенный объект dataSource, который теперь содержит все необходимые
        // параметры для подключения к базе данных
    }


    //LocalSessionFactoryBean — это класс из библиотеки Spring, который предоставляет конфигурацию для Hibernate.
    // Он создает SessionFactory, который используется для получения сессий Hibernate.
    @Bean  //SessionFactory: Создает сессии для работы с Hibernate
    public LocalSessionFactoryBean getSessionFactory() { //LocalSessionFactoryBean Этот класс
        // является частью Spring и используется для настройки Hibernate SessionFactory.
        LocalSessionFactoryBean factoryBean = new LocalSessionFactoryBean();
        factoryBean.setDataSource(getDataSource());// Этот метод устанавливает DataSource,
        // который будет использоваться для подключения к базе данных. В данном случае, он вызывает
        // метод getDataSource(), который мы рассматривали ранее, чтобы получить настроенный объект DataSource.

        Properties props = new Properties();  //Properties является подклассом класса Hashtable, что означает,
        // что он наследует все методы Hashtable .В Properties ключи и значения хранятся в виде строк
        props.put("hibernate.show_sql", env.getProperty("hibernate.show_sql"));
        //Этот код устанавливает свойство hibernate.show_sql, которое определяет, будет ли Hibernate выводить SQL-запросы
        // в консоль. Значение берется из файла свойств с помощью env.getProperty("hibernate.show_sql").
        props.put("hibernate.hbm2ddl.auto", env.getProperty("hibernate.hbm2ddl.auto"));
        // Этот код устанавливает свойство hibernate.hbm2ddl.auto, которое управляет автоматическим созданием и
        // обновлением схемы базы данных. Значение также берется из файла свойств.

        factoryBean.setHibernateProperties(props);//метод устанавливает свойства Hibernate, которые будут
        // использоваться при создании сессий.
        factoryBean.setAnnotatedClasses(User.class);//Этот метод указывает классы, которые будут использоваться
        // Hibernate для создания таблиц в базе данных
        return factoryBean;
        //Этот метод возвращает настроенный экземпляр LocalSessionFactoryBean, который будет
        // использоваться для создания сессий Hibernate. В Spring это обычно делается в методе
        // конфигурации, помеченном аннотацией @Bean, чтобы Spring мог управлять жизненным циклом
        // этого объекта.
    }

    @Bean  //TransactionManager: Управляет транзакциями, обеспечивая целостность данных.
    public HibernateTransactionManager getTransactionManager() { //HibernateTransactionManager — это
        // реализация интерфейса PlatformTransactionManager, которая обеспечивает поддержку транзакций
        // для Hibernate.
        HibernateTransactionManager transactionManager = new HibernateTransactionManager();
        transactionManager.setSessionFactory(getSessionFactory().getObject());
        //Метод setSessionFactory(...) используется для установки SessionFactory, который будет
        // использоваться HibernateTransactionManager для управления сессиями Hibernate
        //Этот метод возвращает объект типа LocalSessionFactoryBean
        //Метод getObject() вызывается на объекте, возвращаемом getSessionFactory().
        // Этот метод возвращает фактический объект SessionFactory, который будет использоваться
        // HibernateTransactionManager.
        return transactionManager;//возвращается настроенный экземпляр HibernateTransactionManager
    }
}