# Spring-data Helper

<a href="https://www.buymeacoffee.com/pPobB4i" target="_blank"><img src="https://www.buymeacoffee.com/assets/img/custom_images/orange_img.png" alt="Buy Me A Coffee" style="height: 51px !important;width: 217px !important;" ></a>

    Problem: You need to make a custom query using Springdata ok?
    you will do: @Query(value = "your query here", nativeQuery = true)
                 List<Object> myCustomQuery();
    Right, this works fine.
    but the result will be:
        [
            {1,
            "name of user",
            "2019-12-22",
            true},
            ...
        ]
    Then you need to parse this in your DTO class right ?
    With this class we give to you a map with column name with java pattern
    without any anotation or mapping.
    then result will be:
        [
            {
                id: 1,
                name: "name of user",
                createdAt: "2019-12-22",
                isActive: true
            },
            ...
        ]          
        
# Tecnologies
    Java 1.8
    Maven 3.6.x
    SOLID && KISS

# Build
    mvn clean install
    mvn -DskipTests clean install
    
# Add in your project
        <dependency>
            <groupId>work.iwacloud</groupId>
            <artifactId>springdata-helper</artifactId>
            <version>1.0.3</version>
        </dependency>
        
        In your Application.java add scanBasePackages
        @SpringBootApplication(scanBasePackages = {"your.package", "work.iwacloud"})

    
# Main developers
    Tiago Henrique Iwamoto - https://www.linkedin.com/in/tiago-iwamoto/
    
# Any help ?
    Members contacts
