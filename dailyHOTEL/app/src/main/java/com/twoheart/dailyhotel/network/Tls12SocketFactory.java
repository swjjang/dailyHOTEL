package com.twoheart.dailyhotel.network;

import com.daily.base.util.ExLog;
import com.daily.base.util.VersionUtils;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

import okhttp3.ConnectionSpec;
import okhttp3.OkHttpClient;
import okhttp3.TlsVersion;

public class Tls12SocketFactory extends SSLSocketFactory
{
    private static final String[] TLS_V12_ONLY = {"TLSv1.2"};

    private final SSLSocketFactory mDelegate;

    public Tls12SocketFactory(SSLSocketFactory sslSocketFactory)
    {
        mDelegate = sslSocketFactory;
    }

    @Override
    public String[] getDefaultCipherSuites()
    {
        return mDelegate.getDefaultCipherSuites();
    }

    @Override
    public String[] getSupportedCipherSuites()
    {
        return mDelegate.getSupportedCipherSuites();
    }

    @Override
    public Socket createSocket(Socket s, String host, int port, boolean autoClose) throws IOException
    {
        return patch(mDelegate.createSocket(s, host, port, autoClose));
    }

    @Override
    public Socket createSocket(String host, int port) throws IOException, UnknownHostException
    {
        return patch(mDelegate.createSocket(host, port));
    }

    @Override
    public Socket createSocket(String host, int port, InetAddress localHost, int localPort) throws IOException, UnknownHostException
    {
        return patch(mDelegate.createSocket(host, port, localHost, localPort));
    }

    @Override
    public Socket createSocket(InetAddress host, int port) throws IOException
    {
        return patch(mDelegate.createSocket(host, port));
    }

    @Override
    public Socket createSocket(InetAddress address, int port, InetAddress localAddress, int localPort) throws IOException
    {
        return patch(mDelegate.createSocket(address, port, localAddress, localPort));
    }

    private Socket patch(Socket socket)
    {
        if (socket instanceof SSLSocket)
        {
            ((SSLSocket) socket).setEnabledProtocols(TLS_V12_ONLY);
        }
        return socket;
    }

    private static OkHttpClient.Builder enableTls12OnPreLollipop(OkHttpClient.Builder client)
    {
        if (!VersionUtils.isOverAPI22())
        {
            try
            {
                SSLContext sslContext = SSLContext.getInstance("TLSv1.2");
                sslContext.init(null, null, null);
                client.sslSocketFactory(new Tls12SocketFactory(sslContext.getSocketFactory()));

                ConnectionSpec connectionSpec = new ConnectionSpec.Builder(ConnectionSpec.MODERN_TLS).tlsVersions(TlsVersion.TLS_1_2).build();

                List<ConnectionSpec> specs = new ArrayList<>();
                specs.add(connectionSpec);
                specs.add(ConnectionSpec.COMPATIBLE_TLS);
                specs.add(ConnectionSpec.CLEARTEXT);

                client.connectionSpecs(specs);
            } catch (Exception e)
            {
                ExLog.e("OkHttpTLSCompat : Error while setting TLS 1.2" + e);
            }
        }

        return client;
    }

    public static OkHttpClient getOkHttpClient()
    {
        OkHttpClient.Builder client = new OkHttpClient.Builder().followRedirects(true).followSslRedirects(true).retryOnConnectionFailure(true);

        return Tls12SocketFactory.enableTls12OnPreLollipop(client).build();
    }
}
