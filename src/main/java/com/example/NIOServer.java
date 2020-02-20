package com.example;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Set;

public class NIOServer {
    public static void main(String[] args) throws Exception {
        new NIOServer().start();
    }

    public void start() throws Exception {
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.bind(new InetSocketAddress(9090));
        serverSocketChannel.configureBlocking(false);
        Selector selector = Selector.open();
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        while (true) {
            selector.select();
            Set<SelectionKey> selectionKeys = selector.selectedKeys();
            Iterator<SelectionKey> iterator = selectionKeys.iterator();
            while (iterator.hasNext()) {
                SelectionKey selectionKey = iterator.next();
                handleKey(selectionKey);
                iterator.remove();
            }
        }
    }

    private void handleKey(SelectionKey selectionKey) throws Exception {
        if (selectionKey.isAcceptable()) {
            handleAccept(selectionKey);
        } else if (selectionKey.isReadable()) {
            handleRead(selectionKey);
        }
    }

    private void handleRead(SelectionKey selectionKey) throws Exception {
        SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        socketChannel.read(buffer);
        System.out.println(new String(buffer.array()));

        ByteBuffer outBuffer = ByteBuffer.wrap("msg received".getBytes());
        socketChannel.write(outBuffer);
    }

    private void handleAccept(SelectionKey selectionKey) throws Exception {
        ServerSocketChannel serverSocketChannel = (ServerSocketChannel) selectionKey.channel();
        SocketChannel socketChannel = serverSocketChannel.accept();
        socketChannel.configureBlocking(false);
        socketChannel.register(selectionKey.selector(), SelectionKey.OP_READ);
    }
}
