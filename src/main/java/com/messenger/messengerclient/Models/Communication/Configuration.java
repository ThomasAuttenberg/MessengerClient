package com.messenger.messengerclient.Models.Communication;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

public class Configuration {
    static SSLSocketFactory factory;
    static final String host = "localhost";
    static final int port = 9000;
    public static void configure() {
        try {

            KeyStore keyStore = KeyStore.getInstance("JKS");
            char[] password = "qwerty".toCharArray();
            String keyStorePathString = "src/main/resources/keystore.jks";

            Path keyStorePath = Paths.get(keyStorePathString).toAbsolutePath();

            keyStore.load(new FileInputStream(keyStorePath.toFile()), password);

            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init(keyStore);

            SSLContext sslContext = SSLContext.getInstance("TLSv1.3");
            sslContext.init(null, trustManagerFactory.getTrustManagers(), null);

            factory = sslContext.getSocketFactory();
        } catch (CertificateException | KeyStoreException | IOException | NoSuchAlgorithmException |
                 KeyManagementException e) {
            throw new RuntimeException(e);
        }
    }
    public static SSLSocket getSocket(int port){
        try {
            return (SSLSocket) factory.createSocket(host, port);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
