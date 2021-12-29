package modules;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.*;
import java.util.Hashtable;

public class TestLdap {

    private static String URL_LDAP = "ldap://192.168.56.103:389";
    private static String DOMAIN_GROUP = "dc=example,dc=com";
    private static String DN_ADMIN = "cn=admin," + DOMAIN_GROUP;
    private static String ADMIN_PASS = "password";

    private static String USERNAME = "henry doe";
    private static String USER_PASS = "password";
    private static String USER_GROUP = "people";

    public static void main(String[] args) {
        String message;

        // if you know that`s user exist, you can use this method
//        message = (!makeSimpleConnection() ? "login error" : "login successfully");

        // if you don`t know that`s user exist, you can use this method
        message = (!makeComplexConnection() ? "login error" : "login successfully");

        System.out.println(message);
    }

    public static boolean makeSimpleConnection() {
        // Set up the environment for creating the initial context
        Hashtable<String, String> env = new Hashtable<>();
        env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        env.put(Context.PROVIDER_URL, URL_LDAP);
        env.put(Context.SECURITY_AUTHENTICATION, "simple");

//        env.put(Context.SECURITY_PRINCIPAL, DN_ADMIN); // FOR DOCKER VERSION -> https://anselmoborges.medium.com/openldap-server-no-tempo-de-um-miojo-825be49e795c
        env.put(Context.SECURITY_PRINCIPAL, (USER_GROUP + "\\" + USERNAME)); //we have 2 \\ because it's a escape char
        env.put(Context.SECURITY_CREDENTIALS, USER_PASS);

        try {
            // Create the initial context
            DirContext ctx = new InitialDirContext(env);
            boolean result = ctx != null;
            ctx.close();
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean makeComplexConnection() {
        Hashtable<String, String> environment = new Hashtable<>();
        environment.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        environment.put(Context.PROVIDER_URL, URL_LDAP);
        environment.put(Context.SECURITY_AUTHENTICATION, "simple");
        environment.put(Context.SECURITY_PRINCIPAL, DN_ADMIN);
        environment.put(Context.SECURITY_CREDENTIALS, ADMIN_PASS);

        try {
            DirContext adminContext = new InitialDirContext(environment);
            String filter = "(&(objectClass=inetOrgPerson)(cn=" + USERNAME + "))";
            String searchControls = searchResult(adminContext, filter);
            if (searchControls == null) System.out.println("User search results null");

            environment.put(Context.SECURITY_PRINCIPAL, searchControls);
            environment.put(Context.SECURITY_CREDENTIALS, USER_PASS); // insert pass for user founded
            DirContext userContext = new InitialDirContext(environment); // and doing connection with user
            boolean verifyLogin = userContext != null;
            userContext.close();

            return verifyLogin;
        } catch (NamingException | NullPointerException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static String searchResult(DirContext adminContext, String filter) throws NamingException {
        // Search for objects using the filter
        String[] attrIDs = {"cn"};
        SearchControls searchControls = new SearchControls();
        searchControls.setReturningAttributes(attrIDs);
        searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);

        NamingEnumeration<SearchResult> searchResults
                = adminContext.search(DOMAIN_GROUP, filter, searchControls);

        String commonName = null;
        String distinguishedName = null;

        if (searchResults.hasMore()) {
            SearchResult result = searchResults.next();
            Attributes attrs = result.getAttributes();

            distinguishedName = result.getNameInNamespace();
            commonName = attrs.get("cn").toString();
            if (commonName != null) System.out.println(commonName);
        }
        adminContext.close();
        return distinguishedName;
    }
}
