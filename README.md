# LDAP TOOLS FOR TESTS


## Steps
- Use [Vagrant](https://www.google.com/url?sa=t&rct=j&q=&esrc=s&source=web&cd=&cad=rja&uact=8&ved=2ahUKEwiG8f6uxYn1AhXOpZUCHVmFBz8QFnoECAwQAQ&url=https%3A%2F%2Fwww.vagrantup.com%2Fdocs%2Finstallation&usg=AOvVaw1nyxrB5IQXsnJ-hoXUbqja) to start up a VM with LDAP pre-configured. (For more details read Vagrantfile and provision.sh)
```shell
vagrant up
```

- Run a phpLDAPadmin docker image to get visual data, replacing ldap.example.com with your ldap host or IP:
```shell
docker run -p 6443:443 \
--env PHPLDAPADMIN_LDAP_HOSTS=ldap.example.com \
--detach osixia/phpldapadmin:0.9.0
```

- Change this items in TestLDAP.java 
```shell
    private static String URL_LDAP = "ldap://YOUR_IP:389";
    private static String DOMAIN_GROUP = "dc=redhat,dc=com";
    private static String DN_ADMIN = "cn=admin," + DOMAIN_GROUP;
    private static String ADMIN_PASS = "admin";

    private static String USERNAME = "rhuser";
    private static String USER_PASS = "password";
    private static String USER_GROUP = "People";
```
- and run TestLDAP to test the LDAP server:
```mvn
mvn clean install
```
```java
java -jar target/ldap-1.0.jar
```

---

