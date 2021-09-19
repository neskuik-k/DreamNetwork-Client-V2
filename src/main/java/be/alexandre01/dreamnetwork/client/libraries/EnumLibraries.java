package be.alexandre01.dreamnetwork.client.libraries;

public enum EnumLibraries {
    COMMONS_IO("https://repo1.maven.org/maven2/commons-io/commons-io/2.11.0/commons-io-2.11.0.jar"),
    COMMONS_LOGGING("https://repo1.maven.org/maven2/commons-logging/commons-logging/1.2/commons-logging-1.2.jar"),
    UNIREST("https://repo1.maven.org/maven2/com/mashape/unirest/unirest-java/1.4.9/unirest-java-1.4.9.jar"),
    JCOLOR("https://repo1.maven.org/maven2/com/diogonunes/JColor/5.0.1/JColor-5.0.1.jar"),
    ASYNCHTTPCLIENT("https://repo1.maven.org/maven2/org/asynchttpclient/async-http-client/2.2.0/async-http-client-2.2.0.jar"),
    GSON("https://repo1.maven.org/maven2/com/google/code/gson/gson/2.8.8/gson-2.8.8.jar"),
    LOGBACK_CLASSIC("https://repo1.maven.org/maven2/ch/qos/logback/logback-classic/1.2.6/logback-classic-1.2.6.jar"),
    DOCKER_JAVA("https://repo1.maven.org/maven2/com/github/docker-java/docker-java/3.2.12/docker-java-3.2.12.jar"),
    JANSI("https://repo1.maven.org/maven2/org/fusesource/jansi/jansi/2.17.1/jansi-2.17.1.jar"),
    JLINE("https://repo1.maven.org/maven2/jline/jline/2.14.6/jline-2.14.6.jar"),
    HTTPCORE_NIO("https://repo1.maven.org/maven2/org/apache/httpcomponents/httpcore-nio/4.4.14/httpcore-nio-4.4.14.jar"),
    HTTPCLIENT("https://repo1.maven.org/maven2/org/apache/httpcomponents/httpclient/4.5.13/httpclient-4.5.13.jar"),
    HTTPCLIENT_MIME("https://repo1.maven.org/maven2/org/apache/httpcomponents/httpmime/4.5.13/httpmime-4.5.13.jar"),
    APACHE_HTTPASYNCLIENT("https://repo1.maven.org/maven2/org/apache/httpcomponents/httpasyncclient/4.1.4/httpasyncclient-4.1.4.jar"),
    HTTPCORE("https://repo1.maven.org/maven2/org/apache/httpcomponents/httpcore/4.4.14/httpcore-4.4.14.jar"),
    GUAVA("https://repo1.maven.org/maven2/com/google/guava/guava/30.1.1-jre/guava-30.1.1-jre.jar"),
    NETTY_ALL("https://repo1.maven.org/maven2/io/netty/netty-all/4.1.68.Final/netty-all-4.1.68.Final.jar"),
    JSON("https://repo1.maven.org/maven2/org/json/json/20210307/json-20210307.jar");


    

    private String url;
    EnumLibraries(String url) {
        this.url = url;
    }

    public String getUrl(){
        return this.url;
    }
}
