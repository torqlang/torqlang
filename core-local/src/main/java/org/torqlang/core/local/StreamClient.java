/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.local;

import org.torqlang.core.actor.ActorRef;
import org.torqlang.core.actor.Address;
import org.torqlang.core.actor.Envelope;
import org.torqlang.core.klvm.Complete;
import org.torqlang.core.klvm.CompleteRec;
import org.torqlang.core.klvm.Eof;
import org.torqlang.core.klvm.Nothing;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.torqlang.core.local.ActorSystem.createRequest;

public class StreamClient implements StreamClientInit, StreamClientResponse {
    private Address address;
    private StreamClientActor streamClientActor;
    private Queue<Envelope> mailbox;
    private CountDownLatch eofLatch = new CountDownLatch(0);

    public static StreamClientInit builder() {
        return new StreamClient();
    }

    @Override
    public final Address address() {
        return address;
    }

    @Override
    public final Queue<Envelope> awaitEof(long timeout, TimeUnit unit) throws Exception {
        if (!eofLatch.await(timeout, unit)) {
            throw new IllegalStateException("Await interrupted");
        }
        return mailbox;
    }

    @Override
    public final Queue<Envelope> mailbox() {
        return mailbox;
    }

    @Override
    public StreamClientResponse send(ActorRef actorRef, Complete message) {
        if (streamClientActor == null) {
            if (address == null) {
                throw new IllegalStateException("Null address");
            }
            eofLatch = new CountDownLatch(1);
            streamClientActor = new StreamClientActor();
        } else {
            if (eofLatch.getCount() > 0) {
                throw new IllegalStateException("Request is already active");
            }
            eofLatch = new CountDownLatch(1);
        }
        mailbox = new ConcurrentLinkedQueue<>();
        actorRef.send(createRequest(message, streamClientActor, Nothing.SINGLETON));
        return this;
    }

    @Override
    public final StreamClientInit setAddress(Address address) {
        this.address = address;
        return this;
    }

    private class StreamClientActor implements ActorRef {
        public StreamClientActor() {
        }

        @Override
        public final Address address() {
            return address;
        }

        @Override
        public final void send(Envelope envelope) {
            mailbox.add(envelope);
            if (envelope.message() instanceof CompleteRec completeRec) {
                if (completeRec.label() == Eof.SINGLETON) {
                    eofLatch.countDown();
                }
            }
        }
    }

}
