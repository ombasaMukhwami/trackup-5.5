package org.traccar.broker;

import com.rabbitmq.client.ConnectionFactory;

public class RabbitOptions {
    private final   String hostName;
    private final   String username;
    private final   String password;
    private  String vHost;
    private int port;
    private  ConnectionFactory factory;

    public RabbitOptions(String hostName, String username, String password, String vHost, int  port) {
        this.hostName = hostName;
        this.username = username;
        this.password = password;
        this.setVirtualHost(vHost);
        this.setPort(port);
        this.setFactory();
    }

    public String getHostName() {
        return hostName;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getVirtualHost() {
        return vHost;
    }

    private void setVirtualHost(String vHost) {
        if (vHost == null) {
            vHost = "/";
        }
        this.vHost = vHost;
    }

    public int getPort() {
        return port;
    }

    private void setPort(int port) {
        if (port == 0) {
            port = 5672;
        }
        this.port = port;
    }

    public ConnectionFactory getFactory() {
        return factory;
    }

    private void setFactory() {
        factory = new ConnectionFactory();
        factory.setHost(getHostName());
        factory.setPassword(getPassword());
        factory.setUsername(getUsername());
        factory.setVirtualHost(getVirtualHost());
        factory.setPort(getPort());
    }
}
