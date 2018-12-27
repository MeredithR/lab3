/*
 * Copyright 2012 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package ServerField;

import Wrapper.Wrapper;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Handles both client-side and server-side handler depending on which
 * constructor was called.
 */
public class ServerHandler extends ChannelInboundHandlerAdapter {
    private ServerField server;

    @Override
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

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
