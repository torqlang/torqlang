/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.examples;

import org.torqlang.core.actor.ActorRef;
import org.torqlang.core.klvm.Str;
import org.torqlang.core.local.Actor;
import org.torqlang.core.local.RequestClient;

import java.util.concurrent.TimeUnit;

public final class HelloWorldWithGoodbye extends AbstractExample {

    public static final String SOURCE = """
        actor HelloWorld() in
            handle ask 'hello' in
                'Hello, World!'
            end
            handle ask 'goodbye' in
                'Goodbye, World!'
            end
        end""";

    public static void main(String[] args) throws Exception {
        new HelloWorldWithGoodbye().performWithErrorCheck();
        System.exit(0);
    }

    @Override
    public final void perform() throws Exception {

        ActorRef actorRef = Actor.builder().spawn(SOURCE).actorRef();

        Object response = RequestClient.builder()
            .sendAndAwaitResponse(actorRef, Str.of("hello"), 100, TimeUnit.MILLISECONDS);

        checkExpectedResponse(Str.of("Hello, World!"), response);

        response = RequestClient.builder()
            .sendAndAwaitResponse(actorRef, Str.of("goodbye"), 100, TimeUnit.MILLISECONDS);

        checkExpectedResponse(Str.of("Goodbye, World!"), response);
    }

}
