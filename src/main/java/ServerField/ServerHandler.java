
package ServerField;

import Wrapper.Wrapper;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.ArrayList;
import java.util.HashMap;


public class ServerHandler extends ChannelInboundHandlerAdapter {
    private ServerField server;

   
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        Wrapper msg1 = (Wrapper) msg;
        ArrayList<Object> data = msg1.getData();
        String user = (String) data.get(0);
        switch (msg1.getStage()) {
            case 1:
                server = new ServerField(user, new Account((Long) data.get(2), (String) data.get(1)));
                ctx.write("Account have created!!");
                break;
            case 2:
                long aBig = (Long) data.get(1);
                if (aBig != 0) {
                    server.receive(user, aBig);
                    server.generateB(user);
                    ctx.write(new Wrapper(1, null, server.accounts.get(user).getbBig() + server.accounts.get(user).getUser_salt()));
                } else {
                    ctx.close();
                    throw new RuntimeException("Error");
                }
                if (!"0".equals(server.scrambler(user))) {
                    server.keyCompute(user);
                    server.confirmationHash(user);
                    ctx.write(new Wrapper(2, null, server.accounts.get(user).getM()));
                } else {
                    ctx.close();
                }

                break;
            case 3:
                if (data.get(1).equals(server.accounts.get(user).getM())) {
                    server.compR(user);
                    ctx.write(new Wrapper(3, null, server.accounts.get(user).getR()));
                }
                break;
            case 4:
                if (data.get(1).equals(server.accounts.get(user).getR())) {
                    System.out.println("Its real client!!!");
                } else {
                    ctx.close();
                }
                break;
        }
        ctx.flush();
    }

   
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

  
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
