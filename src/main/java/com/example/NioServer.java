package com.example;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Set;

/**
 * 基于NIO的服务端
 *
 * @author gaozhuo
 * @date 2020/2/20
 */
public class NioServer {
    public static void main(String[] args) throws Exception {
        new NioServer().start();
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
        } else if (selectionKey.isWritable()) {
            handleWrite(selectionKey);
        }
    }

    private void handleWrite(SelectionKey selectionKey) throws Exception {
        SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
        ByteBuffer outBuffer = ByteBuffer.wrap("msg received".getBytes());
        socketChannel.write(outBuffer);
        selectionKey.interestOps(SelectionKey.OP_READ);
    }

    private void handleRead(SelectionKey selectionKey) throws Exception {
        SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        socketChannel.read(buffer);
        System.out.println(new String(buffer.array()));
        selectionKey.interestOps(SelectionKey.OP_WRITE);

//        ByteBuffer outBuffer = ByteBuffer.wrap("msg received".getBytes());
//        socketChannel.write(outBuffer);
    }

    private void handleAccept(SelectionKey selectionKey) throws Exception {
        ServerSocketChannel serverSocketChannel = (ServerSocketChannel) selectionKey.channel();
        SocketChannel socketChannel = serverSocketChannel.accept();
        socketChannel.configureBlocking(false);
        socketChannel.register(selectionKey.selector(), SelectionKey.OP_READ);
    }
}
