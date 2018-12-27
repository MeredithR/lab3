package ClientField;

import Wrapper.Wrapper;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


public class ClientHandler extends ChannelInboundHandlerAdapter {
    private ClientField client;
   
    public ClientHandler() {

    }

  
    public void channelRegistered(ChannelHandlerContext ctx) throws RuntimeException {
        String id = "";
        String pass = "";
        Scanner in = new Scanner(System.in);
        System.out.println("Enter Username: ");
        if (in.hasNextLine()) {
            id = in.nextLine();
        }
        System.out.println("Enter password: ");
        if (in.hasNextLine()) {
            pass = in.nextLine();
        }
        if (id.length() > 3 && pass.length() > 3) {
            client = new ClientField(id, pass);
        } else {
            ctx.close();
            throw new RuntimeException("Short pass or ID!!!");
        }
        ArrayList<Object> data = new ArrayList<Object>();
        data.add(client.getId());
        data.add(client.getSalt());
        data.add(client.getPass_verifier());
        Wrapper wrapper = new Wrapper(1, data, null);
        ctx.write(wrapper);
    }


    
    public void channelActive(ChannelHandlerContext ctx) {
        ctx.flush();
        ArrayList<Object> data = new ArrayList<Object>();
        data.add(client.getId());
        data.add(client.compA());
        ctx.write(new Wrapper(2, data, null));
        ctx.flush();
    }


  
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        if (msg instanceof String) {
            System.out.println(msg);
        } else {
            Wrapper msg1 = (Wrapper) msg;
            switch (msg1.getStage()) {
                case 1:
                    String str = (String) msg1.getObject();
                    str = str.split(client.getSalt())[0];
                    long b = Long.parseLong(str);
                    if (b != 0) {
                        client.setbBig(b);
                        if (!"0".equals(client.scrambler())) {
                            client.keyComp();
                            String a = client.confirmationHash();
                            ArrayList<Object> data = new ArrayList<Object>();
                            data.add(client.getId());
                            data.add(a);
                            ctx.write(new Wrapper(3, data, null));
                        } else {
                            ctx.close();
                            throw new RuntimeException("1");
                        }
                    } else {
                        ctx.close();
                        throw new RuntimeException("2");
                    }

                    break;
                case 2:
                    String s = (String) msg1.getObject();
                    String s2 = client.getM();
                    if (s.equals(s2)) {
                        ArrayList<Object> data = new ArrayList<Object>();
                        data.add(client.getId());
                        data.add(client.compR());
                        ctx.write(new Wrapper(4, data, null));
                    } else {
                        ctx.close();
                        throw new RuntimeException("3");
                    }
                    break;
                case 3:
                    if (msg1.getObject().equals(client.getR())) {
                        System.out.println("Its real server!!!");
                    } else {

                        ctx.close();
                        throw new RuntimeException("4");
                    }
                    break;

            }
        }

    }

    
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

  
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
